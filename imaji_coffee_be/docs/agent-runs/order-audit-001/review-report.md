# Final review — order-audit-001 / AUD-003 approach A

**Verdict: READY TO COMMIT — scoped AUD-003 change only**<br>
Review date: 2026-11-24<br>
No blocking defect was found in the approved implementation. This verdict is not approval to commit the entire working tree or a certification of overall application production readiness.

## 1. Review scope, inputs, and diff base

Read `prompts/review.md` and the existing audit, approved implementation plan, and implementation report:

- [audit-report.md](audit-report.md), including final classifications and AUD-003 reproduction evidence.
- [implementation-plan.md](../AUD-003/implementation-plan.md), including approach A, acceptance criteria, and explicit approval at lines 136–156.
- [implementation-report.md](../AUD-003/implementation-report.md), including pre-fix failures, implementation results, and integration limits.

There is no separate test report in the agent-run directories. Inspected the existing Maven/Surefire results and reran the approved focused unit suite during this review.

Repository root: `/Volumes/ToanSandisk1TB/imaji_coffee`. Review working directory: its `imaji_coffee_be` subdirectory. Diff base: current `main` HEAD, **`6f1a72fe638458380442e42df8ef0a72f2122142`**, compared with the working tree; the index was also checked separately. Git reports main up to date with the locally recorded origin/main. No remote fetch was performed or required for reviewing this uncommitted change.

Executed all requested Git commands: `git status`, `git diff --stat`, `git diff`, `git diff --staged`, and `git diff --check`. Also inspected the untracked test files directly, because ordinary diff/stat/check output does not include them.

### Working-tree observations

- Tracked diff: four files, **69 insertions and 79 deletions**. This includes the pre-existing parent roadmap edit, which dominates the statistics.
- Scoped tracked source/test diff: PromoPricingService, PromoPricingSnapshot, and PromoPricingServiceTest: **20 insertions and 6 deletions**.
- New untracked regressions: `order/OrderFreeShippingPricingTest.java` and `services/cart/CartServiceImplTest.java` under the test package root. Both were inspected in full.
- Other untracked paths: `AGENTS.md`, `docs/`, and `prompts/`; these were present before implementation. Task reports inside docs are relevant documentation, while instruction/prompt files are not automatically part of the application fix.
- `git diff --staged`: empty. `git diff --check`: exit 0, no whitespace errors in the tracked diff.
- No application/test edits, staging, commits, pushes, migrations, or live payment operations were performed during review. Only this review report was written; the unit run regenerated ignored build/test artifacts under target.

For evidence below, `J/` means `src/main/java/com/duytoan/imajicoffee/imaji_coffee_be/` and `T/` means `src/test/java/com/duytoan/imajicoffee/imaji_coffee_be/`.

## 2. Findings

No new High/Medium code defect or required correction was established in the AUD-003 delta. The following are non-blocking observations, with explicit distinctions between scope hygiene, coverage, and integration risks.

| ID | Severity / classification | Evidence and condition | Suggested action / blocking status |
| --- | --- | --- | --- |
| REV-001 | Low — confirmed commit-scope observation | `git status`/`git diff` show unrelated `../PROJECT_REVIEW_AND_ROADMAP.md` changes; both new regression classes remain untracked. The implementation report documents the pre-existing roadmap change at line 13. A broad future stage/commit could include unrelated work or omit tests if only tracked modifications are selected. | When the user authorizes a commit, select the AUD-003 source, tests, and intended documentation explicitly. Exclude the unrelated roadmap/instruction changes unless independently requested. This is not a code-fix blocker; no staging was performed. |
| REV-002 | Low — optional test-coverage improvement | New consumer fixtures use subtotal=10.00 and quantity=1 (`T/services/cart/CartServiceImplTest.java:48-56`; `T/order/OrderFreeShippingPricingTest.java:93-98`). Boundary cases cover zero/large/cent shipping, but do not cover multiple product lines, fractional-tax rounding, or switching from an existing merchandise coupon to free shipping. Existing percentage rounding coverage is `T/services/promo/PromoPricingServiceTest.java:173-184`. | Optional follow-up cases can strengthen broader pricing assurance. Not required for this minimal fix: the exact shipping cancellation is independent of subtotal, and subtotal/tax/merchandise-discount/promo-replacement code is unchanged. No failure was observed. |
| REV-003 | Low — known integration risk, explicitly approved | Gross shipping now remains nonzero for a free-shipping coupon (`J/services/promo/PromoPricingService.java:107-109`), while a matching discount offsets it. This is the exact representation approved in the plan at line 154. Existing templates show separate shipping/discount lines; the focused tests do not exercise the external UI. | Before deployment, verify client display of shipping plus discount is understandable. Do not change the approved representation without a new decision. Unknown client rendering does not invalidate this approved backend contract correction or block committing it. |

### Existing risks outside the change

The audit's remaining payment authentication, event-ordering, cancellation, transaction, and inventory risks remain unresolved. In particular, AUD-001's conditional blank-secret PayPal bypass is not fixed here (`J/controller/payment/PaymentWebhookController.java:71-89`; audit final-classification table). Nothing in the AUD-003 diff changes these code paths. Their existence is not a regression from this patch and does not authorize expanding the approved scope; this READY TO COMMIT verdict applies to AUD-003, not a blanket production-release recommendation.

No new secret, external call, logging behavior, dependency, security/authorization change, transaction boundary, lock, query, or shared mutable state was introduced by the reviewed delta.

## 3. Correctness and consistency assessment

### Exactly one shipping benefit

`PromoPricingService.evaluate` rounds the incoming shipping charge at `J/services/promo/PromoPricingService.java:89`. The free-shipping case now assigns that amount to discount at lines 107–109 and retains it as shipping in the returned snapshot. Both monetary fields therefore contain the same rounded amount, H.

The existing consumer equation remains:

```text
total = round(subtotal + tax + H - H, 2) = subtotal + tax
```

Thus shipping is waived once. The change removes the previous extra subtraction from merchandise/tax. The exemption from the merchandise-discount cap at line 116 remains appropriate for shipping greater than subtotal: a shipping waiver can equal 20.00 for merchandise worth 10.00 without reducing merchandise below its price.

### End-to-end amount trace within the backend

| Boundary | Evidence | Review result |
| --- | --- | --- |
| Cart pricing | `J/services/cart/impl/CartServiceImpl.java:170-207` | Real evaluator supplies gross shipping and waiver; both top-level cart total and nested promo-validation total subtract the benefit once. |
| Checkout pricing | `J/services/order/impl/OrderServiceImpl.java:389-422` | Uses saved-item prices, rounds subtotal/tax/shipping, applies the same real evaluator and total formula. |
| Order save/response | Order service :111-117, :699-716 | Shipping, discount, and total are copied from the same breakdown into the order and response. Tests inspect repository save arguments, not actual DB commits. |
| Payment request | Order service :125-130 | Uses the same total multiplied by 100; two-decimal BigDecimal total produces whole cents in the tested cases. |
| Stripe SDK boundary | `J/services/payment/impl/PaymentServiceImpl.java:34-43` | Forwards amount/currency/orderId/idempotency key from the request. No SDK or provider change is present; transport is mocked in the tests. |

For matching product/shipping/promo inputs, cart=checkout=order=11.00 and the payment request is 1100 USD cents. Cart previews are not a durable price lock: product/shipping/promo data can change before checkout, and the existing authoritative checkout recalculation remains unchanged.

### Shipping, promotions, rounding, and combined discounts

| Scenario | Result / evidence |
| --- | --- |
| Paid shipping, no promo | subtotal 10 + tax 1 + shipping 5 = **16.00**; both new consumer suites test it. |
| Free shipping, charge 5 | shipping 5, discount 5, total **11.00**, payment request **1100**. |
| Free shipping, charge 0 | shipping 0, discount 0, total **11.00**. |
| Free shipping, charge 20 | shipping 20, discount 20, total **11.00**, with no erroneous zero-total clamp. |
| Free shipping, charge 2.35 | both shipping and discount 2.35, total **11.00**; two-decimal scale asserted. |
| Percentage merchandise promo, paid shipping | discount 1, shipping 5, total **15.00**, payment **1500**; unchanged branch. |
| Fixed merchandise promo, paid shipping | discount 3, shipping 5, total **13.00**, payment **1300**; existing discount-cap test also passes. |
| Rounding | HALF_UP subtotal/tax/shipping and percentage rounding branches are unchanged. Both shipping terms use the same rounded H, so free-shipping cancellation introduces no additional rounding step or cent mismatch. Fractional-tax/multi-item composition is not newly tested; see REV-002. |
| Multiple coupons | OrderRequestDto has a single couponCode (`J/dto/order/OrderRequestDto.java:25-26`); cart holds one Promo. Cart applyPromoCode either rejects a prohibited replacement or assigns one promo at :129-134; it does not accumulate coupon discounts. A stackable flag does not create a coupon list. This pre-existing behavior is unchanged. |
| Catalog price plus coupon | Cart subtotal reads product.getPrice; order snapshots use product.getPrice (`J/services/order/impl/OrderItemServiceImpl.java:70`). No second discount is derived from oldPrice. Free shipping cancels H irrespective of how the current product price was chosen; simultaneous merchandise-coupon plus shipping-coupon stacking is not supported by this path and was not added/tested. |

### API and historical-data compatibility

No request/response DTO, controller, endpoint, status, entity mapping, or schema definition changed. PromoPricingSnapshot received documentation only; its record components are unchanged (`J/services/promo/PromoPricingSnapshot.java:7-23`).

The approved observable correction is that accepted free-shipping responses show original shipping, a matching discount, and the corrected total. Tax remains 10% of pre-discount subtotal; currency remains USD. Existing stored order/payment snapshots are not updated or backfilled. The existing idempotent-order branch returns its stored data before new pricing at `J/services/order/impl/OrderServiceImpl.java:90-96`; the separate early-validation replay issue remains outside this fix.

## 4. Approval and acceptance checklist

The explicit approach-A approval was verified in `docs/agent-runs/AUD-003/implementation-plan.md:136-156`. The production change is exactly the proposed removal of shipping zeroing, plus its comment/record documentation. Existing cart/order totals require no special-case code changes, as planned.

| Criterion | Status | Evidence / limits |
| --- | --- | --- |
| Only approved AUD-003 evaluator convention, documentation, and focused tests changed | PASS for scoped delta | Two production files (one executable change and documentation) plus three test files. Unrelated pre-existing roadmap diff is separately identified. |
| Example produces 11.00/1100 rather than 6.00/600 | PASS | Fresh unit run; order test :109, :128-136; cart test :75, :92-104. |
| Cart, promo validation, order save/response, and payment request agree | PASS at unit boundary | Real evaluator in both consumer suites; captured payment DTO and saved-order arguments. Real ORM/provider settlement remains unverified. |
| Shipping discount does not reduce merchandise/tax again | PASS | Retained equal shipping/discount; hardcoded expected total and high-shipping case. |
| Zero/large/cent shipping boundaries pass | PASS | Four evaluator scenarios plus matching cart/card scenarios. |
| Paid shipping, fixed/percentage promos, eligibility, usage, rounding, non-card behavior preserved | PASS within scoped coverage | Existing branches unchanged; 64 focused tests pass, including invalid free-shipping and COD/PayPal checks. Broader coupon combinations are constrained as described above. |
| API shapes/schema/dependencies unchanged | PASS | Complete source diff: no DTO/controller/entity/pom/migration changes. Value correction is explicitly approved. |
| Historical order/intent amounts remain untouched | PASS by code inspection | No migration/reconciliation changes; existing-order lookup still returns persisted amounts. No live data was inspected or changed. |
| Tests executed safely and actual outcomes recorded | PASS | Review rerun: 64 tests, no failures/errors/skips; no Spring application/DB/Stripe/SMTP transport started. |
| New code has no unrelated edits/secrets | PASS for scoped delta | Source/test inspection; unrelated roadmap edit existed beforehand. Entire working tree is not a single approved commit. |
| External client display, live Stripe, DB transaction behavior | UNKNOWN / not required for scoped commit | Not tested; known integration limits, not a new claim of production verification. |

No approved acceptance criterion failed. Broader integration coverage is not represented as passed.

## 5. Test evidence and quality

### Fresh review execution

Executed the plan's exact focused suite outside the sandbox so Mockito's existing inline JVM agent could attach:

```sh
./mvnw -Dtest=PromoPricingServiceTest,CartServiceImplTest,OrderFreeShippingPricingTest,OrderServiceImplTest,OrderConfirmationEmailModelFactoryTest,OrderConfirmationEmailTemplateRendererTest test
```

**BUILD SUCCESS — 64 tests; 0 failures; 0 errors; 0 skipped.** Review run completed at **2026-11-24 10:01:29 -05:00**, Maven total time 2.578 seconds. Six classes: OrderServiceImplTest 27, OrderFreeShippingPricingTest 13, CartServiceImplTest 8, PromoPricingServiceTest 12, and two email classes with 2 each. No application/test edits were needed for the rerun.

### Do these regressions detect the bug?

**Yes, for the original monetary defect and its approved representation.**

- The new tests instantiate the real PromoPricingService inside real cart/order services (`T/services/cart/CartServiceImplTest.java:43-45`; `T/order/OrderFreeShippingPricingTest.java:69-73`). They do not stub an already-correct PromoPricingSnapshot.
- Public getCart/applyPromoCode/createOrder methods are exercised. Hardcoded business expectations such as 11.00 and 1100 are independent of the current returned values. The extra formula assertions are consistency checks, not the sole oracle.
- The original implementation would fail the nonzero-shipping free-shipping cases. Implementation-report line 59 records the actual pre-fix run: 9 assertion failures out of 21 consumer cases, showing 6.00/0.00/8.65 instead of 11.00. That red run is historical implementation evidence; this read-only review did not revert source to reproduce it again.
- Shipping=20 exceeds the merchandise subtotal and catches the former erroneous zero-total clamp, while shipping=0 guards the boundary where old/new totals happen to agree.
- Card tests capture the outgoing PaymentIntentRequestDto and verify amount/currency/orderId/idempotency key, rather than only checking the response (`T/order/OrderFreeShippingPricingTest.java:130-136`).
- Saved-order assertions inspect the mocked repository's object. This verifies service amount propagation but does not prove ORM flush/commit correctness. Mocked item persistence similarly keeps the regression focused on pricing, not stock reservation.
- Evaluator tests check the approved breakdown and a composed total; consumer tests separately execute the real production formulas, mitigating the risk of merely copying the implementation into a unit-test oracle.

### Remaining coverage limits

Optional fractional-tax/multi-item/promo-replacement cases are noted in REV-002. The card/cart tests are separate fixtures rather than one shared cross-service integration fixture, but they use identical hardcoded scenarios and verify the same expected amounts. No true multi-coupon stacking, external client rendering, HTTP serialization, provider settlement, database transaction, or concurrency run was performed. The full Spring/database suite was deliberately not run.

## 6. Release recommendation

**READY TO COMMIT** for the explicitly scoped AUD-003 approach-A source, regression tests, and intended task documentation. The change matches the approved plan, fixes the demonstrated undercharge once, preserves other executable pricing branches, and passes the independently rerun focused suite.

Keep the new test classes in any future scoped commit and keep the pre-existing roadmap/instruction changes separate unless the user directs otherwise. Resolve broader release decisions and other audit findings separately. This review neither stages nor authorizes an automatic commit/push/deployment. Stop for the user's decision.
