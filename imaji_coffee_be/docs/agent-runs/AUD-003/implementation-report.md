# AUD-003 — Implementation report

**Status: COMPLETED — stopped after the approved fix**<br>
Date: 2026-11-24<br>
Approved finding: AUD-003, approach A<br>
Plan: [implementation-plan.md](implementation-plan.md)<br>
Source audit: [order-audit-001](../order-audit-001/audit-report.md)

## Scope and approval

Read `prompts/implement.md` and verified the plan's explicit user approval of AUD-003/approach A before editing. The user's subsequent instruction authorized beginning implementation. Only the single shipping-waiver correction, its documentation, and the approved focused regression coverage were implemented.

Initial Git status already contained a modified `../PROJECT_REVIEW_AND_ROADMAP.md` and untracked `AGENTS.md`, `docs/`, and `prompts/`. Those unrelated edits were not overwritten. No commits, pushes, migrations, deployments, real payments, or SMTP operations were performed.

## Files changed and purpose

Java paths are relative to `src/main/java/com/duytoan/imajicoffee/imaji_coffee_be/`; test paths are relative to `src/test/java/com/duytoan/imajicoffee/imaji_coffee_be/`.

| File | Change |
| --- | --- |
| `services/promo/PromoPricingService.java` | Removed the free-shipping branch's assignment that zeroed shipping after already assigning the same charge as discount. Added a comment explaining the single deduction. |
| `services/promo/PromoPricingSnapshot.java` | Documented shipping before promotion and the once-subtracted discount; record signature unchanged. |
| Test: `services/promo/PromoPricingServiceTest.java` | Updated free-shipping expectation and parameterized original/zero/large/cent-precision shipping cases; assert monetary scale and composed total. |
| New test: `services/cart/CartServiceImplTest.java` | Eight public cart-method cases with the real promo evaluator: totals and nested promo-validation breakdown, applyPromoCode, free-shipping boundaries, no promo, fixed, and percentage promos. |
| New test: `order/OrderFreeShippingPricingTest.java` | Thirteen real-evaluator checkout cases: captured payment amount/identity, saved order/response breakdowns, boundaries, unchanged promo types, COD/PayPal branches, and unusable free-shipping rejection before payment. Persistence, address, payment, and mail boundaries are mocked. |
| `docs/agent-runs/AUD-003/implementation-plan.md` | Recorded completion and acceptance results while retaining the approval history. |
| This implementation report | Records scope, verification, compatibility, and remaining limits. |

No cart/order/controller/payment-service logic, DTO signatures, schemas, configuration, dependencies, tax rules, inventory logic, or lifecycle transitions were changed.

## Behavior and compatibility

For a valid free-shipping promotion, the original shipping charge remains in shippingAmount and the same amount appears in discountAmount. Consumers add shipping and subtract the waiver once.

| Audit example | Before | After |
| --- | --- | --- |
| Subtotal | 10.00 | 10.00 |
| Tax | 1.00 | 1.00 |
| Shipping | 0.00 | 5.00 |
| Discount | 5.00 | 5.00 |
| Total | 6.00 | 11.00 |
| New card intent amount | 600 cents | 1100 cents |

The shipping breakdown value intentionally changes as approved. Cart total, promo-validation total, order response, and repository save arguments agree. Card request amount/currency/orderId/idempotency key are checked. No actual Stripe request is sent by the tests.

API fields, endpoint shapes, and status conventions remain unchanged. Existing order/payment snapshots are not repriced or edited. Fixed/percentage/no-promo pricing, eligibility/usage constraints, pre-discount tax, rounding, and COD/PayPal behavior are preserved by unchanged application branches and focused regression checks.

## Commands executed and actual results

1. Read implementation instructions/approved plan and inspected Git status and relevant source/tests.
2. Added consumer regression tests before applying the application fix, then ran:

   ```sh
   ./mvnw -Dtest=CartServiceImplTest,OrderFreeShippingPricingTest test
   ```

   Initial sandbox execution compiled the tests but could not initialize Mockito's inline mock maker because its JVM agent could not self-attach: **21 initialization errors**. This was an environment failure, not evidence of AUD-003.

3. Reran the same focused command outside the sandbox after tool approval: **21 cases, 9 assertion failures, 0 errors**. Failing totals were 6.00, 0.00, or 8.65 where the approved total was 11.00. These failures reproduced AUD-003 before the fix; the other 12 cases passed.
4. Applied the approved evaluator change, snapshot documentation, and evaluator-test update, then ran outside the sandbox:

   ```sh
   ./mvnw -Dtest=PromoPricingServiceTest,CartServiceImplTest,OrderFreeShippingPricingTest,OrderServiceImplTest,OrderConfirmationEmailModelFactoryTest,OrderConfirmationEmailTemplateRendererTest test
   ```

   **BUILD SUCCESS: 64 tests, 0 failures, 0 errors, 0 skipped.** Compilation used Java release 17 on JDK 21.0.10.

   | Class | Passing cases |
   | --- | --- |
   | OrderServiceImplTest | 27 |
   | OrderFreeShippingPricingTest | 13 |
   | CartServiceImplTest | 8 |
   | PromoPricingServiceTest | 12 |
   | OrderConfirmationEmailModelFactoryTest | 2 |
   | OrderConfirmationEmailTemplateRendererTest | 2 |

5. Inspected tracked changes and both new test classes; `git diff --check` exited 0. Git status showed only the scoped source/test edits plus the pre-existing unrelated changes and audit documentation. No unrelated application changes or secrets were introduced.

The build emitted existing Lombok/deprecation warnings in unrelated classes and Mockito/JVM dynamic-agent notices. No build-configuration changes were made to address them.

## Test limits and remaining risks

- Tests use Mockito/repository save arguments and the real promotion evaluator; they do not establish ORM commit/rollback behavior or real provider settlement.
- Full-suite/database/concurrency integration tests were not run. AUD-003 changes only invocation-local pricing arithmetic and adds no transaction/locking state.
- External client rendering was not exercised. Clients must show the approved gross shipping and matching discount together.
- Other audit findings remain outside this fix. Historical audit reproduction code intentionally describes the old behavior and is not a post-fix acceptance suite.
- Reverting this arithmetic change would restore the undercharge for future free-shipping calculations; it would not correct historical records.

All approved AUD-003 work is complete. Stop here for user review.
