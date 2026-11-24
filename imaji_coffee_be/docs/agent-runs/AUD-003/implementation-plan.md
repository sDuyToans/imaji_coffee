# AUD-003 — Correct free-shipping total calculation

**Status: IMPLEMENTED — approved AUD-003 fix completed; awaiting user review**

Date: 2026-11-24<br>
Selected finding: **AUD-003**, confirmed defect, High severity<br>
Source audit task: **order-audit-001**<br>
Audit: [audit-report.md](../order-audit-001/audit-report.md)<br>
Requested plan destination: `docs/agent-runs/AUD-003/implementation-plan.md`

The user approved approach A for AUD-003 and subsequently instructed implementation. The scoped evaluator correction, documentation, and regression tests are complete; see [implementation-report.md](implementation-report.md). No dependencies, database records, migrations, or live payment operations were changed or executed.

## 1. Root cause and evidence

All Java paths below are relative to `src/main/java/com/duytoan/imajicoffee/imaji_coffee_be/`. Test paths are relative to `src/test/java/com/duytoan/imajicoffee/imaji_coffee_be/`.

The shared promotion evaluator represents free shipping twice: it sets the discount to the shipping charge and also replaces the shipping charge with zero. Its consumers add the returned shipping and subtract the returned discount, granting an additional discount against merchandise/tax.

| Source | Relevant behavior |
| --- | --- |
| `services/promo/PromoPricingService.java:107-109` | `free_shipping` assigns `discount = safeShipping` and then zeros `safeShipping`. |
| `services/order/impl/OrderServiceImpl.java:395-422` | Calculates tax on subtotal, consumes both returned amounts, then computes `subtotal + tax + shipping - discount`. |
| `services/cart/impl/CartServiceImpl.java:187-207` | Uses the same returned amounts for both promo-validation total and cart total. |
| `services/order/impl/OrderServiceImpl.java:111-134` | Persists the breakdown and passes total multiplied by 100 to card PaymentIntent creation. |
| `services/promo/PromoPricingSnapshot.java:7-15` | Carries shipping and discount independently; it does not state whether shipping is gross or net of promotion. |
| `services/promo/PromoPricingServiceTest.java:120-132` under test root | Existing free-shipping test expects shipping=0 and discount=5, without asserting the final total produced by consumers. |

The audit's isolated reproduction used the real pricing/checkout method bodies with fake persistence and payment collaborators. For subtotal 10.00, tax 1.00, and shipping 5.00, the current total was 6.00 and the outgoing payment request was 600 cents. No real payment was made. Database concurrency and transaction behavior were not tested by that reproduction and are not root causes of this arithmetic defect.

## 2. Expected behavior and proposed approach

Free shipping should waive the shipping charge exactly once. For an eligible free-shipping promo:

```text
shippingAmount = original shipping charge, rounded to two decimals
discountAmount = shippingAmount
totalAmount    = subtotal + existing tax + shippingAmount - discountAmount
               = subtotal + existing tax
```

For the audit example, the proposed breakdown is subtotal=10.00, tax=1.00, shipping=5.00, discount=5.00, total=11.00. A new card checkout must request 1100 USD cents.

**Recommended minimal change:** remove the assignment that zeros `safeShipping` in the evaluator's `free_shipping` case. Keep `discount = safeShipping`. Add a short comment clarifying that shipping remains the charge before promotion because consumers subtract the shipping benefit through discountAmount.

Document this representation on `PromoPricingSnapshot`: shippingAmount is the charge before the promotion; discountAmount is subtracted once by consumers and may include a shipping waiver. This clarifies an existing contract without introducing fields or new pricing abstractions.

Both cart and order already use the required total formula; their application logic should need no edit. Validate that assumption through public cart and checkout methods using the real evaluator. Do not duplicate a special-case total formula in each consumer.

### Compatibility and scope

- Request/response DTO shapes, types, endpoint paths, authorization rules, and status codes remain unchanged.
- For accepted free-shipping promos, the **numeric meaning/value of shippingAmount changes** from zero to the original charge; discountAmount retains the original charge and total increases by the previously duplicated benefit. This is an observable breakdown correction, not an assertion of unchanged response values.
- `PromoValidationDto.shipping`, `CartDto.shipping`, order responses, persisted order breakdowns, and confirmation emails for newly created orders reflect that representation. Existing email templates already show shipping and discount as separate lines; template edits are not proposed.
- Existing subtotal, 10% pre-discount tax, USD currency, HALF_UP rounding, and total floor remain as implemented. Fixed/percentage promo behavior, eligibility restrictions, usage accounting, inventory, and lifecycle transitions remain unchanged.
- Existing orders and already-created PaymentIntents retain their recorded amounts. There is no backfill, repricing, collection, refund, or modification of prior customer commitments in this plan.
- Existing cart reads recalculate from current product/shipping/promo data, so affected carts show corrected totals on their next read. The normal checkout response remains the authoritative new payable amount.
- No schema migration, configuration change, new dependency, controller change, or payment-service change is needed.

## 3. Alternatives and trade-offs

| Approach | Benefits | Costs / reason for decision |
| --- | --- | --- |
| **A. Keep gross shipping; discount shipping once — recommended** | Small evaluator change fixes every discovered consumer; retains the explicit promo benefit amount; keeps the current additive/subtractive total formula. | Free-shipping response shipping field becomes nonzero. Clients must display shipping and discount together rather than assume shipping alone is the final shipping cost. Requires approval of this numeric representation. |
| B. Zero shipping and set discount to zero | Retains a zero shipping field and yields the same correct final total. | Discount becomes zero despite an accepted promo; loses explicit saved-shipping value in existing discount fields and confirmation breakdowns. Could be selected instead if client display semantics require net shipping. |
| C. Keep evaluator output and special-case free shipping in cart/order totals | Could retain existing shipping=0 and discount=shipping response values. | Breakdown would no longer obey `subtotal + tax + shipping - discount`; changes several calculations and invites drift. More invasive and internally inconsistent. Not recommended. |
| D. Introduce separate merchandise/shipping discounts or a pricing engine | Makes categories explicit and supports future discounts. | DTO/schema/client changes and broader architecture exceed the single arithmetic fix. Defer. |

Do not combine A and B: either representation can waive shipping once, but mixing zeroed shipping with the shipping discount recreates AUD-003.

## 4. Ordered implementation steps and affected files

Implementation is one bounded AUD-003 change, performed only after approval.

1. **Add failing consumer regressions using the real evaluator.** Add the two proposed test classes below. Exercise the public cart and checkout methods, not copied formulas or mocked pricing snapshots. Verify the example produces 11.00 and the card request contains 1100 cents. Observe the current 6.00 result causing the regression failure.
2. **Correct the evaluator and its documented amount convention.** Apply approach A to the free-shipping branch; add brief snapshot documentation. Update the existing evaluator test's shipping expectation to 5.00 and retain discount=5.00. Add an explicit monetary invariant/final-total assertion.
3. **Verify boundary and unaffected promo cases.** Add the focused cases below with parameterized tests where appropriate. Keep existing mocked-order tests intact; use a dedicated test class for real-evaluator composition rather than changing every existing test's injection strategy.
4. **Run focused local checks, inspect the diff, and stop.** Record actual commands/results in an implementation report in this finding's run directory. Do not extend the change into other audit findings or silently adjust a consumer/API representation beyond this approved plan. If consumer changes become necessary, stop and revise the plan.

| File | Planned action |
| --- | --- |
| `services/promo/PromoPricingService.java` | Remove shipping-zero assignment for free shipping; explain the single-discount convention. |
| `services/promo/PromoPricingSnapshot.java` | Add concise field/record documentation; no signature or field change. |
| `services/promo/PromoPricingServiceTest.java` under test root | Correct existing expectation; add monetary invariant and zero/large shipping boundaries; retain existing eligibility/fixed/percentage cases. |
| **New:** `services/cart/CartServiceImplTest.java` under test root | Use real CartServiceImpl and PromoPricingService with repository mocks; assert cart and nested promo-validation breakdowns/totals agree through getCart/applyPromoCode. |
| **New:** `order/OrderFreeShippingPricingTest.java` under test root | Use real OrderServiceImpl and PromoPricingService; mock repository/item-persistence/address/mail/payment boundaries. Capture PaymentIntentRequestDto; assert response and saved order amounts. Include COD/PayPal branch checks with no outgoing Stripe call. |

Read/verify without planned edits: CartServiceImpl, OrderServiceImpl, payment request/response DTOs, PromoValidationDto, CartDto, and existing confirmation model/templates. Mocked item persistence returns valid persisted-product snapshots; no request-supplied price is used. Pure unit tests must not start a Spring context, database, Stripe SDK transport, or mail transport.

## 5. Regression test matrix

Unless specified otherwise, fixtures use subtotal=10.00, existing tax=1.00, and original shipping=5.00. Test BigDecimal numerically and also verify monetary scale where relevant.

| Category | Case | Expected result / assertion |
| --- | --- | --- |
| Success | Eligible free-shipping evaluator | accepted=true; shipping=5.00; discount=5.00; composed total=11.00. |
| Success | Cart public getCart with accepted promo | Cart total=11.00; shipping=5.00; discount=5.00; nested promo-validation total/breakdown agree. |
| Success | Cart public applyPromoCode | Accepted promo persists through mocked repository and returned cart uses the same corrected breakdown; no second waiver. |
| Success | Card createOrder with real evaluator | Saved order and response total=11.00, shipping=5.00, discount=5.00; captured intent amount=1100, currency=usd; existing orderId/idempotency-key propagation retained. |
| Success | COD and PayPal creation with same promo | Same pricing as card; no Stripe creation. COD mail is mocked; PayPal remains the shared local creation flow. Do not expand either lifecycle. |
| Boundary | Shipping=0.00 | shipping=0.00; discount=0.00; total=11.00. |
| Boundary | Shipping=20.00, greater than subtotal | shipping=20.00; discount=20.00; total=11.00; no erroneous clamp to zero and no merchandise discount. |
| Boundary | Cent precision, shipping=2.35 | shipping=discount=2.35; exact net waiver; two-decimal outputs. Existing percentage-rounding test remains green. |
| Failure | Expired/inactive/ineligible or exhausted free-shipping promo | Existing rejection/hint preserved. New checkout must not invoke payment creation after rejection; no test should claim mock calls prove database rollback. Reuse existing evaluator rejection tests rather than duplicate the entire eligibility suite. |
| Compatibility | No promo | total=16.00; discount=0.00; shipping=5.00. |
| Compatibility | 10% merchandise promo | discount=1.00; shipping=5.00; total=15.00. |
| Compatibility | Fixed merchandise discount=3.00 | discount=3.00; shipping=5.00; total=13.00. Existing discount-cap test remains green. |
| Concurrency | Applicability assessment | The evaluator change is per-invocation arithmetic; it adds no shared mutable state, lock, or transaction boundary. No new threaded/DB test is justified for AUD-003. Existing concurrency findings remain separate and are not declared resolved by these tests. |

Proposed focused command after implementation:

```sh
./mvnw -Dtest=PromoPricingServiceTest,CartServiceImplTest,OrderFreeShippingPricingTest,OrderServiceImplTest,OrderConfirmationEmailModelFactoryTest,OrderConfirmationEmailTemplateRendererTest test
```

The two new classes will exist only during approved implementation. Confirm all selected tests remain pure mocks/unit tests before running; do not run the full Spring/database suite or `clean verify` against a shared database. If cached dependencies are insufficient or compilation fails, record the actual blocker rather than claiming tests passed. No test/build command was executed while preparing this plan.

## 6. Risks and rollback strategy

**Client interpretation:** The gross-shipping convention is the main compatibility decision. Clients that display shipping independently of discount may show a shipping charge alongside a free-shipping promo. Approval must explicitly accept approach A; if net shipping is required, choose B and revise tests before implementation. This repository does not establish all external client assumptions.

**Financial consistency:** New accepted free-shipping orders must use the same corrected amount in cart, response, persistence, and PaymentIntent request. Tests must use the real evaluator at consumer boundaries so the defect cannot be hidden behind a mocked pricing snapshot. Do not alter existing orders or intents, including idempotent responses for an already-created order.

**Inventory/usage/security:** Preserve existing locks, transaction annotations, stock changes, promo usage checks and consumption, authorization, signature checks, and provider mapping. These have separate audited issues; this plan neither changes nor certifies them.

**Rollback:** The proposed change is code/test/documentation only and requires no data rollback. Reverting a deployed evaluator change would reintroduce undercharging for future free-shipping orders, so it is not a harmless financial rollback. Preserve all existing order/payment snapshots; choose a reviewed corrective release or operational promo suspension separately if needed. No deployment, suspension, commit, push, or production action is authorized here.

## 7. Acceptance checklist

- [x] Only AUD-003's evaluator convention, its documentation, and focused regression tests change.
- [x] Audit example produces total=11.00 and a 1100-cent USD card request, never 6.00/600.
- [x] Cart, promo-validation response, saved order, and order response obey `subtotal + tax + shipping - discount = total` for accepted fixtures.
- [x] Free-shipping discount equals original shipping and never additionally reduces merchandise/tax.
- [x] Zero shipping, shipping greater than subtotal, and cent-precision boundaries pass.
- [x] No-promo, fixed/percentage promo, eligibility, usage limits, tax, currency, rounding, and payment-method behavior are preserved.
- [x] Endpoint/DTO signatures and database schema/dependencies are unchanged; changed free-shipping breakdown values are documented.
- [x] Existing order and PaymentIntent amounts remain untouched; no migration or historical financial adjustment occurs.
- [x] Focused pure unit regressions pass with actual results recorded; no real payment, database, SMTP, or production operation occurs.
- [x] Diff contains no unrelated changes or secrets; pre-existing working-tree edits remain untouched.

## 8. Approval record and authorized scope

Approval recorded: **2026-11-24 14:40:11 UTC**. Approved by: **the user, explicitly in this conversation**.

User instruction:

> I approve the first proposed fix in docs/agent-runs/AUD-003/implementation-plan.md.
>
> Record my approval in the implementation plan, including the approved finding ID and scope.
>
> Do not modify application code yet.

**Approved finding:** AUD-003 only — double subtraction of the free-shipping benefit.

**Approved approach:** A, the first proposed fix. Keep original shipping in shippingAmount and represent the shipping waiver once in discountAmount; remove the evaluator's assignment that also zeros shipping. For subtotal=10.00, tax=1.00, and shipping=5.00, the approved result is total=11.00 and a 1100-cent USD card request.

**Approved implementation scope for a subsequent start instruction:** the evaluator correction and snapshot documentation, the existing evaluator test update, and the focused cart/checkout regression tests specified in sections 4–5. Preserve public DTO/API shapes, existing tax/rounding/currency rules, promotion eligibility and usage, inventory/lifecycle behavior, and existing order/payment snapshots. No other findings, alternatives B–D, migrations, dependencies, historical financial adjustments, deployments, commits, or pushes are approved.

**Original execution limit at approval recording:** documentation only; application/test edits were deferred until the subsequent start instruction. That instruction has now been received and the approved scope completed.

## 9. Implementation completion

Completed on 2026-11-24 after the user explicitly instructed reading this plan, verifying approval/Git status, and implementing the first approved fix. Consumer regressions failed before the correction (9 assertion failures out of 21 cases); the final focused suite passed all 64 cases. All acceptance checklist items above are satisfied within the documented unit-test scope. No database/provider integration or external client rendering was tested. See [implementation-report.md](implementation-report.md) for commands, results, compatibility details, and limits.

**Current execution limit:** stop after AUD-003 for user review. No additional finding, commit, push, migration, or deployment is authorized.
