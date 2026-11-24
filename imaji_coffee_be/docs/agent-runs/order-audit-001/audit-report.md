# Order implementation audit — order-audit-001

Date: 2026-11-24. Target: existing order implementation. Stage: investigation only.

The user explicitly authorized producing this report. Only audit/plan documentation is written; application code, dependencies, migrations, tests, and databases are unchanged. No payment endpoints were contacted. Existing unrelated working-tree changes were left untouched.


## Final classification of every original finding

This section supersedes earlier severity/confidence statements where they differ. Each original ID is classified once below. Totals: **10 confirmed defects, 9 potential risks, 3 missing-coverage findings, and 1 withdrawn false-positive claim**. A conditional defect is confirmed for the stated inputs; it is not a claim that those conditions exist in production. Severity is conditional impact.

| ID | Classification | Severity | Evidence and exact reproduction conditions | Existing safeguards and limits |
| --- | --- | --- | --- | --- |
| AUD-001 | Confirmed defect | Critical | `J/controller/payment/PaymentWebhookController.java:28,71-90`; `J/security/PublicPathConfig.java:23`. Blank configured PayPal secret + fresh completed event + numeric ID of PENDING non-COD order reaches payment mutation without authentication. Isolated controller/service reproduction returns 200/PAID. | Nonblank secret rejects missing/wrong header; unknown orders, COD, duplicates, and invalid transitions are guarded. Real deployment reachability/configuration unverified. |
| AUD-002 | Confirmed defect | High | Webhook controller :77-89; `J/services/order/impl/OrderServiceImpl.java:347-368,559-574`. Event that passes the header check maps to a pending card order; accepts 0.01 EUR payload for a 27 USD order because only identifiers/type are forwarded. Isolated acceptance reproduced. | Header verification when configured, order lock, COD exclusion, allowed transition, event dedup. No independent attacker bypass of a correctly configured secret is claimed. Provider-generated mismatched event not tested. |
| AUD-003 | Confirmed defect | High | `J/services/promo/PromoPricingService.java:107-109`; order service :417-422; cart service :197-207. Eligible free_shipping, subtotal=10, tax=1, shipping=5 yields total=6 and a 600-cent card request instead of one shipping waiver yielding 11. Isolated checkout reproduced. | Server prices, promo validation/limits, product lock, rounding, zero floor remain effective but do not prevent double subtraction. No real charge made. |
| AUD-004 | Confirmed defect | High | Order service :60,299-310,559-569,593-605. Apply distinct failure then success for one mapped intent. Failure releases stock; success and explicit succeeded confirmation reject PAYMENT_FAILED->PAID. Isolated sequential branch reproduced. | Verified Stripe signature at controller, mapping, locks, dedup, stockReleased. Success-before-failure is guarded; actual provider retry/delivery sequence not tested. |
| AUD-005 | Confirmed defect | High | Order service :233-247,380-385,567,624-642. Owner/admin locally cancels pending card order while intent can still succeed; later success throws, later refund is acknowledged without reconciling CANCELLED. Isolated sequence reproduced. | Ownership/admin restrictions, allowed cancellation states, order lock, stockReleased, provider verification on webhook. Real late settlement not attempted; paid-cancellation auto-refund policy is not independently a defect. |
| AUD-006 | Confirmed defect | High | Order service :55,209-228,277-282,559-565; `J/controller/order/OrderController.java:75`. Authorized admin advances unpaid online order to PROCESSING; later success does not set PAID, and explicit confirmation skips retrieval. Isolated sequence reproduced. | ADMIN guard blocks ordinary users; locks/transitions/dedup and email flags exist. Requires an allowed admin action, not privilege escalation. |
| AUD-007 | Potential risk | Medium | Order service :324-336,348-360. Fresh unmapped event is finalized; later replay after mapping is suppressed. Branch reproduced. Operational loss requires a legitimate event before its mapping exists. | Verified-provider boundary, unique event key, normal checkout delivers client secret after completion. No ordinary client confirmation racing its own checkout commit was established. |
| AUD-008 | Confirmed defect | Medium | Order service :82-96,495-505. Successful checkout consumes last unit; same-key replay now fails early stock validation before returning its existing order. Deterministic source trace; not rerun in current isolated harness. | User-scoped existing-order lookup/unique key prevents ordinary duplicate creation once reached. Controller validates structure. These checks cannot restore replay availability when early stock/config validation fails. |
| AUD-009 | Potential risk | High | Webhook controller :48-56; order service :624-650. Eligible refund event discards amount information and restores whole quantities for PAID/PROCESSING/SHIPPED. SHIPPED whole-restock branch reproduced; production defect additionally requires supported partial refund or goods not physically returned. | Signature/mapping, lock, source-state allowlist, REFUNDED/stockReleased guards; DELIVERED explicitly does not restock. Partial-refund and return policy are unknown, so the broader business-defect claim is no longer confirmed. |
| AUD-010 | Confirmed defect | Medium | `J/dto/address/AddressDto.java:39`; `J/services/address/impl/AddressServiceImpl.java:54-60`. Existing matching address + omitted/null isDefault passes DTO validation then unboxes null and throws. Source-deterministic; not executed in current harness. | User ownership lookup and transaction exist. Non-null flags and new-address branch avoid this specific unboxing path; address service is mocked in existing checkout tests. |
| AUD-011 | Confirmed defect | Medium | Address service :54-62 versus :71-74. Existing default A and nondefault matching B; checkout requests B as default. Reuse branch sets B without clearing A. Source-deterministic; no DB run. | User-scoped lookup; new-address branch clears defaults. No corresponding cleanup in reuse branch. Concurrency is not required. |
| AUD-012 | Confirmed defect | Medium | `J/repository/address/AddressRepository.java:52-60`; address service :44-76. Same optional apartment/postal/phone null on both sides is compared using equality rather than a null-safe condition; repeated address use inserts another row. Query semantics/source evidence, no ORM run. | User-scoped lookup prevents cross-user reuse; all-nonnull identical fields can match. No null-safe clause or address uniqueness constraint is shown. No ambiguous-lookup incident is claimed. |
| AUD-013 | Potential risk | High | Order service :80,125-143,577-590; mail service :38-52. Successful external operation precedes later address/commit failure, or mail error aborts payment-event processing. Isolated call order and error-before-event-finalization reproduced. | Transaction rolls back local runtime failures; provider key, event transaction, and sent flag help successful retries. Actual rollback, orphan remote intent, duplicate mail, and crash recovery not reproduced. |
| AUD-014 | False positive as an asserted defect; withdrawn | Unassigned | Order service :88,275,303,332-339,495-505; item service :38-55. Early read + later lock was used to speculate about stale state/overselling. No failing Hibernate/MySQL schedule was demonstrated. | Pessimistic write queries and same-transaction updates exist. This classification withdraws the asserted failure; it does not prove every concurrency schedule safe. Keep only a test question, not a fix recommendation. |
| AUD-015 | Potential risk | Medium | Order service :509-523; item service :37-55. Two overlapping checkouts in reverse product order can contend while each holds its first product lock; external calls prolong lock duration. No deadlock reproduced. | Pessimistic locks prevent concurrent writes to the same locked row; DB abort/rollback and lock-error handling exist. No actual inventory corruption or automatic retry guarantee demonstrated. |
| AUD-016 | Potential risk | Medium | Order service :90,125-130; payment service :34-43; order repository :36. Distinct users/orders reuse same key; recording fake receives same raw provider key with different orderId metadata. Argument mismatch reproduced. | Local user+key uniqueness and existing-order return protect local scope. Provider rejection/collision, global key usage in deployed clients, and rollback retry outcome not tested. |
| AUD-017 | Potential risk | Medium | Order service :90-105,655-673; order/entity and event/entity unique constraints. Concurrent duplicate callers must both pass initial lookup before insertion. Loser may fail rather than receive prior result. No DB race reproduced. | DB unique constraints prevent duplicate durable keys; order locks serialize mapped events; final event-existence recheck can avoid duplicates. A check-then-insert pattern alone does not prove duplicate charges or durable double stock deduction. |
| AUD-018 | Potential risk | Medium | Order service :107-120,244-245,593-650. Abandon online checkout after immediate stock/promo reservation; no local automatic expiry found. Immediate reservation reproduced, indefinite hold not demonstrated. | Explicit cancellation/failure/refund stock release exists; unpaid COD cancellation restores promo usage. External jobs and reservation/promo policy unknown. |
| AUD-019 | Potential risk | Medium | `J/config/db/OrderSchemaMigrationRunner.java:57,147-156`. Historical paid online fulfilled row initially UNPAID remains UNPAID because backfill recognizes only exact PAID/PAYMENT_FAILED fulfillment statuses. Requires such historical rows; none inspected. | Already-PAID payment status is untouched, exact PAID backfills, COD is not incorrectly inferred paid merely from delivery. No proven historical data loss. |
| AUD-020 | Potential risk | Medium | Address DTO :20-29 versus Address entity :28-41; baseline :26-34. Accepted null city/street or >2-character country reaches persistence; city mapping length also differs from baseline. Failure depends on actual schema/ORM path. | @Valid, nonnull name/country, field size/pattern checks, persisted address constraints, transaction. Contract mismatch is confirmed statically; deployed persistence error not reproduced. |
| AUD-021 | Missing test coverage | Medium | `T/product/ConcurrentPurchaseIntegrationTest.java:49-114`. Test calls item service directly, bypasses full checkout, and final assertions permit zero sales. Source inspection; database test not run. | Existing test exercises actual transaction manager/product locks; item unit test rejects insufficient stock. Coverage weakness is not proof of overselling. |
| AUD-022 | Missing test coverage | Medium | `T/order/OrderServiceImplTest.java:51-82` and repository test inventory. No order/payment HTTP trust-boundary tests found; service mocks do not execute filters/signature/JSON validation. | Service ownership-denial tests and implemented role/signature checks exist; missing HTTP coverage does not negate those safeguards. |
| AUD-023 | Missing test coverage | Medium | Promo test :120-132; order test :296-416 and test inventory. No final free-shipping-total, reordered failure/success, explicit confirmation, partial-refund, or commit-fault matrix found. | Existing price recalculation, promo limits, isolated event cases, sequential duplicates, COD lifecycle, and email-render tests cover useful behaviors. Missing matrix is not another runtime defect. |

### Recommendation and decision boundary

**Address AUD-001 first.** It is a demonstrated conditional payment-authentication bypass: with no configured secret, an unauthenticated synthetic request can mark a pending order paid. Unlike the policy-dependent refund and concurrency risks, it does not require an ambiguous inventory rule or an unproven race. The existing nonblank-secret rejection provides a narrow behavior to preserve while making missing verification fail closed. A first fix should be limited to rejecting unverified PayPal webhook requests and regression tests for blank/missing/wrong/correct secret cases; direct-provider versus trusted-relay authentication must be confirmed before introducing a broader integration.

Deployed exposure has not been established. If PayPal is unreachable or correctly guarded in every environment, **AUD-003 is the next confirmed candidate**: reproducible deterministic undercharging on accepted free-shipping checkouts, independent of event ordering.

No application code/tests/migrations were changed. No implementation plan or fix has been approved. Wait for the user's finding selection before planning/implementation.

## 1. Scope, method, and inspected files

This report combines the previously traced lifecycle with a static review of correctness, authorization, concurrency, transaction consistency, inventory, provider integration, failure handling, and input edge cases. “Confirmed” means a behavior or defect follows directly from the cited code and stated inputs, not that a deployed incident was reproduced. “Suspected” means the impact depends on runtime behavior, deployment configuration, or an unresolved business rule. “Test gap” describes missing or insufficient verification, not proof of a production defect.

No Maven tests or application startup were run. Re-examination executed isolated, in-memory current-method-body checks using cached dependencies; the detailed method, results, limits, and runnable harness appear below. No application/test source or database was changed.

Evidence paths in this report are repository-relative. Java paths beginning with `J/` expand to `src/main/java/com/duytoan/imajicoffee/imaji_coffee_be/`; test paths beginning with `T/` expand to `src/test/java/com/duytoan/imajicoffee/imaji_coffee_be/`. Line numbers refer to the inspected checkout.

### Files inspected

- Instructions: `AGENTS.md`, `prompts/audit.md`; dependency/build configuration: `pom.xml`.
- Entry points: `J/controller/order/OrderController.java`, `J/controller/payment/PaymentController.java`, `J/controller/payment/PaymentWebhookController.java`, order-related methods in `J/controller/auth/AccountController.java`.
- Services: `J/services/order/IOrderService.java`, `J/services/order/IOrderItemService.java`, `J/services/order/impl/OrderServiceImpl.java`, `J/services/order/impl/OrderItemServiceImpl.java`, `J/services/payment/IPaymentService.java`, `J/services/payment/impl/PaymentServiceImpl.java`.
- Related services: `J/services/promo/PromoPricingService.java`, `J/services/promo/PromoPricingSnapshot.java`, `J/services/cart/impl/CartServiceImpl.java`, `J/services/address/impl/AddressServiceImpl.java`, `J/services/email/impl/MailServiceImpl.java`, order-confirmation email model factory/renderer, order-related reads in `J/services/admin/ai/AdminAnalyticsService.java`.
- Persistence: `J/entities/order/Order.java`, `J/entities/order/OrderItem.java`, `J/entities/payment/PaymentWebhookEvent.java`, `J/entities/user/Address.java`; order/item repositories, product/promo/promo-usage repositories, address repository, and payment-webhook-event repository.
- Contracts: order request/item/response/detail DTOs, `J/dto/address/AddressDto.java`, payment intent DTOs, `J/enums/OrderStatus.java`, `J/enums/PaymentStatus.java`.
- Configuration and errors: `J/security/SecurityConfig.java`, `J/security/PublicPathConfig.java`, JWT request-filter authentication path, `J/config/StripeConfig.java`, `J/config/db/OrderSchemaMigrationRunner.java`, `J/exceptions/GlobalExceptionHandler.java`.
- Schema/configuration: relevant order/item/address definitions in `src/main/resources/db/migration/V1__baseline.sql`, migration inventory, selected nonsecret configuration keys in application/test properties.
- Tests: `T/order/OrderServiceImplTest.java`, `T/order/OrderItemServiceImplTest.java`, `T/product/ConcurrentPurchaseIntegrationTest.java`, `T/services/promo/PromoPricingServiceTest.java`; existing email tests and repository-wide test inventory/searches for order/payment coverage.

## 2. Current architecture and execution flow

### Entry points and authorization

`OrderController` exposes checkout (`POST /api/v1/order`), the PayPal alias (`POST /api/v1/order/paypal`), order detail, cancellation, admin status updates, admin COD collection, and owner/admin Stripe confirmation. `AccountController` additionally exposes user order history and owner cancellation. Ordinary API access requires a USER or ADMIN role. Detail and cancellation services enforce ownership, with explicit admin bypass. Admin status/collection and standalone PaymentIntent creation have `@PreAuthorize` restrictions. Webhook routes are public at the API authentication layer (`J/security/PublicPathConfig.java:23`), so their controller-level provider authentication is the trust boundary.

JWT identity comes from the token cookie or bearer header. Checkout derives its user ID from authentication and reads email from the persisted user. The request does not supply trusted prices or a customer email.

### Checkout

`OrderServiceImpl.createOrder` (`:79-145`) runs in one transaction:

1. Validate request structure and card configuration. Merge duplicate product IDs; reject missing IDs and nonpositive individual quantities. Read products and check current stock/availability.
2. Look up `(userId, checkoutRequestId)`. Return the existing order and, for card, retrieve its client secret if found.
3. Load user and shipping method; insert a PENDING order with UNPAID payment status, USD currency, JSON address snapshot, shipping description, and checkout key.
4. `OrderItemServiceImpl.saveOrderItems` locks each product, rechecks availability/quantity, decrements stock, and saves item snapshots (product ID/name/image/category/price/quantity).
5. Compute subtotal, 10% tax before discount, shipping, and coupon discount. Round to two decimal places with HALF_UP. Clamp negative totals to zero.
6. If a coupon applies, lock its row, increment its usage count, and insert promo usage immediately, before payment success.
7. Card: create Stripe PaymentIntent using server total in cents, USD, orderId metadata, and the request key; save intent ID and return client secret. COD: synchronously send confirmation email and save its sent flag. PayPal: no outgoing creation/capture call exists in this path.
8. Reuse/save the address in the user's address book; return totals and status. Commit occurs through the Spring transaction interceptor after the method returns.

The PayPal checkout method is only an alias to shared checkout. Checkout uses request items, not the persisted cart; it does not clear the cart. Cart pricing separately shares `PromoPricingService`. `quotePricingForCart` exists on the order service but no caller appeared in source searches.

### Payments, fulfillment, and terminal states

Stripe webhook controller verifies the signature and recognizes PaymentIntent success/failure/cancellation and charge refunds. Order mapping uses the stored intent ID, not event metadata. Explicit Stripe confirmation independently retrieves the intent and checks `succeeded`.

PayPal webhook controller optionally checks a custom shared-secret header and extracts a local numeric order ID from custom/invoice fields. Its service handler excludes COD but otherwise accepts the mapped order. Shared event classification uses substrings: success/completed, failed/denied, cancellation/expired/voided, refunded/reversed.

| Current order state | Allowed next states in the transition map |
| --- | --- |
| PENDING | PROCESSING, PAID, PAYMENT_FAILED, CANCELLED |
| PAID | PROCESSING, CANCELLED, REFUNDED |
| PROCESSING | SHIPPED, CANCELLED, REFUNDED |
| SHIPPED | DELIVERED, REFUNDED |
| DELIVERED | REFUNDED |
| PAYMENT_FAILED / CANCELLED / REFUNDED | None |

Admin updates reject PAID, PAYMENT_FAILED, and REFUNDED targets. Success normally changes PENDING to PAID and sets paymentStatus=PAID; failure changes PENDING to PAYMENT_FAILED/FAILED and restores stock. Cancellation restores stock; only unpaid COD cancellation explicitly restores coupon usage. Refunds set REFUNDED and restore stock unless the previous state was DELIVERED. Cancellation/refund helpers do not update paymentStatus. COD collection requires DELIVERED and sets paymentStatus=PAID without changing fulfillment status.

### Dependencies, database operations, and transactions

Order orchestration injects order, user, shipping, promo, promo-usage, webhook-event, and product repositories; order-item, payment, address, mail, and promo-pricing services; and Jackson. Persistence uses Spring Data JPA/MySQL. External operations use Stripe Java SDK and JavaMailSender. Email model/renderer produce HTML and plain-text confirmation bodies.

| Resource | Operations / boundaries |
| --- | --- |
| orders | Insert; total/intent/status/payment/email/stock-flag updates; owner/key/intent lookups; reporting. Unique user+checkout-key constraint. |
| order_items | Snapshot inserts and detail/reporting reads. Order cascade-all/orphan removal; baseline also has product FK despite no JPA product association. |
| products | Early plain reads; later pessimistic-write lock, stock decrement/restore, save. |
| promos / promo_usage | Locked promo evaluation/counter writes; per-user usage count; usage insert/delete. |
| payment_webhook_event | Existence checks, processed-event insert; unique provider+event constraint. |
| user / shipping / addresses | Checkout lookups, address deduplication/default handling and inserts/updates. |

All public mutation methods on the order service have `@Transactional`; order detail/history and quote use read-only transactions. Item stock methods and address saving join the caller transaction. No custom isolation/timeout/propagation is declared. Product, promo, and order repositories provide pessimistic-write queries. Stripe and synchronous email calls occur before database commit while transaction locks can remain held. Webhook transition and processed-event insert share a transaction, but external mail delivery does not participate in database rollback.

Schema includes Flyway baseline definitions plus a MySQL startup runner that conditionally adds columns/tables/indexes and backfills payment status. Existing analytics select revenue by fulfillment/order status rather than paymentStatus.

### Sequence diagram

```mermaid
sequenceDiagram
    actor Customer
    participant API as OrderController
    participant O as OrderService
    participant I as OrderItemService
    participant DB as MySQL
    participant P as Stripe
    participant M as MailService
    participant A as AddressService
    participant W as WebhookController
    actor Admin
    Customer->>API: Checkout(request)
    API->>O: createOrder(request, authenticated userId)
    Note over O,DB: Begin checkout transaction
    O->>DB: Validate products; lookup user+checkout key
    alt Existing order
        opt Card
            O->>P: Retrieve client secret
        end
    else New order
        O->>DB: Load user/ship; insert PENDING/UNPAID
        O->>I: saveOrderItems
        I->>DB: Lock products; deduct stock; insert snapshots
        O->>DB: Evaluate locked promo; save totals; consume usage
        alt Card
            O->>P: Create intent(server total, currency, metadata, key)
            P-->>O: Intent ID and secret
            O->>DB: Persist intent ID
        else COD
            O->>M: Send confirmation
            O->>DB: Persist sent flag
        else PayPal
            Note over O: No outgoing provider operation here
        end
        O->>A: Save/reuse address
        A->>DB: Address reads/writes
    end
    Note over O,DB: Commit checkout transaction
    API-->>Customer: Order response
    P->>W: Signed event
    W->>W: Verify signature and extract identifiers
    W->>O: handleStripeWebhookEvent
    Note over O,DB: Begin event transaction
    O->>DB: Check event; map and lock order
    O->>DB: Apply transition; restore stock if needed
    opt Success requiring email
        O->>M: Send confirmation
        O->>DB: Save sent flag
    end
    O->>DB: Insert processed event
    Note over O,DB: Commit event transaction
    W-->>P: HTTP acknowledgement
    Admin->>API: Fulfillment update / COD collection
    API->>O: Update requested state
    O->>DB: Lock; validate; save
    Customer->>API: Cancel eligible order
    API->>O: Authorize and cancel
    O->>DB: Lock order; restore stock once; restore unpaid COD promo
```

## 3. Findings

Severity reflects potential business impact, not deployed exploitability. Critical means a payment trust-boundary bypass; High means payment/inventory correctness or substantial financial inconsistency; Medium means recoverability, validation, or availability impact. All suggested fixes are proposals only.

### Confirmed bugs / code-supported defects

| ID | Severity | Confidence | Finding and evidence |
| --- | --- | --- | --- |
| AUD-001 | Critical | confirmed | PayPal webhook authentication fails open when the configured secret is blank. `J/controller/payment/PaymentWebhookController.java:28,71-89`; `J/security/PublicPathConfig.java:23`. |
| AUD-002 | High | confirmed | PayPal events are not bound to payment method, expected amount/currency, or a previously associated provider payment. `J/controller/payment/PaymentWebhookController.java:77-89`; `J/services/order/impl/OrderServiceImpl.java:347-368,533-575`. |
| AUD-003 | High | confirmed | Free-shipping promo removes shipping and subtracts the same shipping amount again. `J/services/promo/PromoPricingService.java:107-109`; `J/services/order/impl/OrderServiceImpl.java:417-422`; `J/services/cart/impl/CartServiceImpl.java:197,201-207`. |
| AUD-004 | High | confirmed | A failure followed by success cannot reconcile: PAYMENT_FAILED is terminal, but failure already returned stock. `J/services/order/impl/OrderServiceImpl.java:60,559-569,593-605,299-310`. |
| AUD-005 | High | confirmed | Local card cancellation does not cancel the provider intent; later payment success is rejected, and refunds after cancellation are ignored. `J/services/order/impl/OrderServiceImpl.java:233-247,61,567,624-642`; payment service exposes create/retrieve only. |
| AUD-006 | High | confirmed | Moving an unpaid online order to PROCESSING prevents later success from recording paymentStatus=PAID. `J/services/order/impl/OrderServiceImpl.java:55,209-228,277-282,559-565`. |
| AUD-008 | Medium | confirmed | Checkout replay validates current stock/configuration before finding the existing checkout. `J/services/order/impl/OrderServiceImpl.java:82-96,495-505`. |
| AUD-010 | Medium | confirmed | Accepted nullable address default flag can throw during checkout address reuse. `J/dto/address/AddressDto.java:39`; `J/services/address/impl/AddressServiceImpl.java:54-60`; `J/services/order/impl/OrderServiceImpl.java:123-143`. |
| AUD-011 | Medium | confirmed | Selecting an existing nondefault address as default does not clear the previous default. `J/services/address/impl/AddressServiceImpl.java:54-62,71-74`. |
| AUD-012 | Medium | confirmed | Address reuse equality does not match nullable fields, allowing repeated identical rows. `J/repository/address/AddressRepository.java:52-60`; `J/services/address/impl/AddressServiceImpl.java:44-76`. |

#### AUD-001 — Unauthenticated PayPal payment mutation

- **Scenario:** Configure the webhook secret as empty (also the controller's default), use a local pending PayPal order, and submit a JSON event containing a success-like event name and its numeric custom_id. No authentication header or application user is required. The handler reaches markPaid.
- **Impact:** A caller who knows an order ID can mark an unpaid order paid or invoke other supported transitions without proving a provider payment. This is a confirmed conditional bypass; deployed secret configuration was not inspected.
- **Suggested fix:** Fail closed when verification is unavailable; authenticate provider events using the chosen verified integration and reject unverified requests before order access. A custom-header relay, if intended, needs an explicit trusted relay contract.

#### AUD-002 — Provider and financial identity not checked

- **Scenario:** Pass a completed PayPal event mapped to a PENDING card order; only COD is excluded. The order becomes PAID. A mapped PayPal order also becomes paid without checking any amount, currency, merchant, or associated capture identifier. The service receives none of those financial fields.
- **Impact:** An event can settle the wrong provider's order or a mismatched/insufficient payment. A valid shared-secret header does not establish that the selected order was paid in full.
- **Suggested fix:** Require the expected provider/payment method, persist provider-order/capture associations, compare trusted financial/merchant fields, and use an explicit allowlist of event meanings rather than generic substrings.

#### AUD-003 — Double free-shipping benefit

- **Scenario:** Subtotal 10.00, tax 1.00, original shipping 5.00. Evaluator returns shipping=0 and discount=5; checkout computes 10+1+0-5=6.00 instead of 11.00 for shipping removal alone. Cart uses the same combination.
- **Impact:** Undercharges by the original shipping charge; large shipping relative to subtotal can clamp payable total to zero.
- **Suggested fix:** Represent the benefit once: retain shipping and discount it, or zero shipping without subtracting it again. Verify final cart and order totals together. The existing evaluator test explicitly expects both intermediate fields and misses this composition defect.

#### AUD-004 — Failure/success ordering and retries

- **Scenario:** Apply payment_intent.payment_failed to a pending order, then payment_intent.succeeded for the same associated intent. Failure sets PAYMENT_FAILED/FAILED and releases stock; success calls validateTransition(PAYMENT_FAILED, PAID), which throws. Explicit confirmation also cannot transition this state after retrieval reports succeeded.
- **Impact:** Money-received state cannot be reconciled by either success path; stock has already been made available. The branch behavior is confirmed; the frequency of provider retry/out-of-order delivery was not measured.
- **Suggested fix:** Separate payment attempts from order terminality, reconcile authoritative payment state, and define stock reacquisition or compensation when a late success arrives after release.

#### AUD-005 — Cancellation and money movement diverge

- **Scenario:** Create a card checkout, retain its client secret, cancel locally, then deliver a success event for the intent. The cancellation method only updates local rows/restores stock; success is invalid from CANCELLED. A subsequent refund event on that cancelled order falls outside markRefunded's allowed source states and is silently recorded as processed. Cancelling an already-paid order likewise performs no provider refund.
- **Impact:** A payment can settle after the stock was returned, or a paid cancellation can retain captured funds without an automated settlement path. Whether paid cancellation should refund immediately is a business-policy question; the absent provider action and rejected reconciliation are confirmed.
- **Suggested fix:** Coordinate provider cancellation/refund and local state with recoverable operations; persist monetary outcomes independently of fulfillment terminal states.

#### AUD-006 — Fulfillment shortcuts swallow payment updates

- **Scenario:** Admin advances a pending unpaid card/PayPal order to PROCESSING, a transition not limited to COD. Success handler sees PROCESSING and only sends/checks email, returning before setting paymentStatus/externalPaymentId. Explicit Stripe confirmation also returns before retrieving provider state.
- **Impact:** The order can progress to shipment while unpaid and remain UNPAID after actual payment. The behavior also undermines financial reporting and confirmation semantics.
- **Suggested fix:** Restrict unpaid fulfillment according to payment-method policy; update monetary state independently of fulfillment state and do not return from confirmation based on fulfillment alone.

#### AUD-008 — Stock-dependent idempotent replay

- **Scenario:** A checkout consumes the final unit. Resend the same valid request/key; early product validation now rejects its quantity before the existing-order lookup. Product removal/unavailability or missing card configuration can similarly block replay.
- **Impact:** The caller cannot reliably recover the original order/client secret after a lost response, despite the stored idempotency key.
- **Suggested fix:** Resolve and validate the existing checkout identity first, then apply mutable inventory/configuration checks only for a new operation; define request-fingerprint behavior for key reuse with different inputs.

#### AUD-009 — Refund and physical stock are conflated

- **Scenario:** Deliver a refund event for a multi-unit PAID/PROCESSING order representing a smaller monetary adjustment. The controller forwards only identifiers; markRefunded sets the whole order REFUNDED and restores every item. A SHIPPED order also restores every item without a return/receipt signal.
- **Impact:** Monetary adjustments can close entire orders and advertise stock still held by the customer/carrier. Current code cannot distinguish partial from full refunds, regardless of the provider payload. Supported refund/return policy remains an open question.
- **Suggested fix:** Track refund amounts/status and returned quantities separately; reconcile cumulative refund totals and require an explicit inventory-return policy/event.

#### AUD-010 — Nullable isDefault causes post-side-effect failure

- **Scenario:** A matching saved address exists and checkout omits isDefault or sends null. Bean Validation accepts it; address reuse unboxes addressDto.isDefault() in an if statement, throwing NullPointerException.
- **Impact:** Checkout rolls back late, after Stripe intent creation or COD email delivery may already have happened. Existing checkout unit tests mock the address service, so they do not exercise this path.
- **Suggested fix:** Normalize absent Boolean to false or validate it explicitly; complete request/address validation before external side effects.

#### AUD-011 — Multiple default addresses

- **Scenario:** User has address A marked default and matching saved address B not default. Checkout reuses B with isDefault=true. Existing-address branch sets B true but never invokes clearDefaultForUser, unlike the new-address branch.
- **Impact:** Multiple addresses can be default, making subsequent address selection inconsistent.
- **Suggested fix:** Apply one consistent default-address update rule to both reuse and insertion, with user-scoped concurrency control if a single default is required.

#### AUD-012 — Null-sensitive address deduplication

- **Scenario:** Checkout twice with the same address whose apartment is null. The JPQL comparison `a.apartment = :apartment` does not match null to null. Postal code/phone have the same issue when omitted. A new row is inserted instead of reusing the prior address.
- **Impact:** Repeated orders accumulate duplicate addresses; this audit did not reproduce an ambiguous Optional lookup.
- **Suggested fix:** Use null-safe equality and define the full address identity, including recipient/province where appropriate. Assess cleanup separately before changing existing user data.

### Potential risks requiring runtime or business confirmation

| ID | Severity | Confidence | Evidence, scenario, impact, suggested fix |
| --- | --- | --- | --- |
| AUD-007 | Medium | suspected | **Unmatched event replay suppression, conditional operational impact.** `J/services/order/impl/OrderServiceImpl.java:324-336,348-360`. A fresh unmatched event is finalized and later replay suppressed. This branch was reproduced; a legitimate early event in normal client-confirmed checkout was not. Client secret is returned after checkout completion. **Proposal:** identify actual unmatched-event sources, then retain/retry unresolved events if required. |
| AUD-009 | High | suspected | **Refund/return policy risk.** Controller discards monetary refund fields; eligible pre-delivery refund restores whole quantities. SHIPPED restock and DELIVERED no-restock were reproduced. A business defect additionally requires a partial refund or absent physical return under the intended policy, which remains unverified. Evidence: webhook controller :48-56; order service :624-650. **Proposal:** establish refund/return policy before choosing separate monetary and stock-return tracking. |
| AUD-013 | High | suspected | **Payment/email effects before commit.** `J/services/order/impl/OrderServiceImpl.java:80,125-143,577-590`; `J/services/email/impl/MailServiceImpl.java:38-52`. If Stripe succeeds then address persistence/commit fails, the DB can roll back while the intent survives. If email succeeds then commit fails, retry can resend because its flag rolled back. Email failure also rolls back payment/event state despite external money movement. **Proposal:** durable pending-payment/outbox workflow, independent notification retry, explicit compensation/reconciliation. Fault injection is needed to quantify behavior. |
| AUD-015 | Medium | suspected | **Inconsistent product-lock ordering and long lock holding.** `OrderServiceImpl.java:509-523` preserves request order; `OrderItemServiceImpl.java:37-55` locks in that order. Checkouts [A,B] and [B,A] can deadlock; provider/mail calls extend the lock duration (`OrderServiceImpl.java:125-143`). No application retry is present in these paths. **Proposal:** deterministic lock order, bounded transactions, and safe retry policy. Reproduce with barriers and a disposable database. |
| AUD-016 | Medium | suspected | **Checkout identity scope differs from Stripe key scope; rollback retry is unstable.** Local key is user-scoped (`J/repository/order/OrderRepository.java:36`; `J/entities/order/Order.java:20-24`), but Stripe receives the raw user key with orderId metadata (`OrderServiceImpl.java:125-130`; `J/services/payment/impl/PaymentServiceImpl.java:34-43`). Different users can reuse a key, and a rollback/retry can generate a new orderId for the same key. Provider idempotency may reject differing parameters instead of recovering checkout. **Proposal:** durable server-generated operation identity, consistently namespaced provider key, and recovery of the same local operation. Provider behavior was not exercised. |
| AUD-017 | Medium | suspected | **Concurrent deduplication is check-then-insert.** Checkout lookup then insert (`OrderServiceImpl.java:90-105`) and event existence checks then insert (`:655-673`) rely on unique constraints rather than conflict recovery. Two simultaneous identical requests/events can both pass initial checks; the loser may return an unhandled conflict/500 (webhook 400) instead of the prior result. Same-order locks mitigate some event interleavings but do not make the initial check atomic; isolation visibility matters. **Proposal:** atomic operation/event claim and explicit duplicate-conflict recovery; test with real transactions. |
| AUD-018 | Medium | suspected | **Reservation expiry is not established in the reviewed backend.** Checkout deducts stock and consumes usage (`OrderServiceImpl.java:107-120`); no scheduled expiry/reconciliation path appeared in main-source searches. Card/PayPal failures and ordinary online cancellations do not call restorePromoUsage (`:244-245,593-642`). Users who abandon payment can exhaust inventory/coupon limits unless an external process intervenes. **Proposal:** agreed reservation TTL and consistent release policy, with reconciliation for late payment. External jobs and business policy are unknown. |
| AUD-019 | Medium | suspected | **Historical payment-status backfill may miss fulfilled paid rows.** `J/config/db/OrderSchemaMigrationRunner.java:57,147-156` maps only exact PAID and PAYMENT_FAILED order states when paymentStatus is UNPAID. Historical paid online PROCESSING/SHIPPED/DELIVERED rows remain UNPAID after introducing this column. **Proposal:** versioned, auditable migration based on reliable historical payment evidence; never infer paid COD from fulfillment alone. Existing data was not read. |
| AUD-020 | Medium | suspected | **Request/address persistence contracts differ.** `J/dto/address/AddressDto.java:20-29` permits country up to 100 characters and null city/street, while `J/entities/user/Address.java:28-41` requires city/street and a two-character country; city itself is annotated length 2 despite the baseline's wider column (`src/main/resources/db/migration/V1__baseline.sql:26`). Accepted payloads can fail during late persistence, depending on deployed schema. **Proposal:** align address semantics, validation, entity mapping, and migration; validate before Stripe/email. |

### Missing or insufficient tests

| ID | Severity | Confidence | Evidence, missing scenario, impact, suggested verification |
| --- | --- | --- | --- |
| AUD-021 | Medium | test gap | **Concurrency integration test bypasses checkout and may pass with zero successful purchases.** `T/product/ConcurrentPurchaseIntegrationTest.java:49-114` calls only saveOrderItems, omitting checkout's early product reads. It catches arbitrary exceptions and permits sold=successes=0; fixture supplies no main product image although snapshot productImg is non-null (`J/services/order/impl/OrderItemServiceImpl.java:62-67`; `J/entities/order/OrderItem.java` productImg mapping). Success counter is also incremented before transaction commit. Thus this test does not establish checkout concurrency safety. **Add:** full-checkout tests with valid fixtures, commit-based counts, stock conservation, at least one/expected successful sale, reverse item order, duplicate keys, and event-vs-cancel interleavings. |
| AUD-022 | Medium | test gap | **No order/payment HTTP trust-boundary tests found.** Repository test inventory contains auth/admin/FAQ controller tests, but no OrderController/PaymentWebhookController test. `T/order/OrderServiceImplTest.java:51-82` uses Mockito-injected service calls, bypassing security, JSON validation, signature verification, and transaction interception. **Add:** anonymous/user/nonowner/admin matrix, forged/missing Stripe signatures, blank/mismatched PayPal verification, cross-provider and amount/currency mismatches. Without them the authorization annotations are reviewed, not behaviorally verified. |
| AUD-023 | Medium | test gap | **No end-to-end pricing/event/fault regression matrix.** `T/services/promo/PromoPricingServiceTest.java:120-132` checks free-shipping intermediate values only; `T/order/OrderServiceImplTest.java:296-416` tests isolated success/failure/refund cases and sequential duplicate delivery. Source searches found no explicit Stripe-confirmation tests, failure-then-success/late-success matrix, partial refunds, unmatched replay, or commit/email/provider fault injection. **Add:** final-total assertions, reordered events, confirmation races, and transactional failures with fake external services. Isolated service mocks cannot prove persistence atomicity or idempotency under concurrency. |

### Reviewed controls and non-findings

- Customer detail/cancellation have service-level owner checks; admin mutations have method-role restrictions. No direct customer IDOR was established from those paths.
- Normal checkout derives amounts from database product snapshots, shipping, and promo evaluation. The client does not choose card checkout amount/currency.
- Stripe webhook uses SDK signature construction before service delegation. No Stripe-signature bypass was identified in the inspected path.
- Order/product/promo write locks and uniqueness constraints exist. No stale-entity/overselling failure was demonstrated; AUD-014 is removed from active findings. Lock-order and duplicate-conflict behavior remain conditional Medium risks.
- Stock restoration checks stockReleased while the order mutation path holds an order lock. Sequential repeated restoration is guarded; delivered refunds do not restock.
- PaymentStatus has only UNPAID, PAID, FAILED. REFUNDED order status retaining PAID paymentStatus was not separately labeled a bug because whether PAID denotes historical collection or current settlement is unspecified.
- Tax-before-discount and fixed USD were documented, not treated as tax-policy defects without business requirements.
- The admin standalone PaymentIntent endpoint accepts its DTO amount/currency and does not attach its created intent to an order. It is admin-restricted; no ordinary-user amount tampering was established. Its intended support workflow remains an open question.


### Re-examination: dispositions and reproducibility

Re-examined on 2026-11-24 at the user's request. IDs are preserved; removed IDs are not reassigned. The active findings now comprise **10 confirmed code-supported defects, 9 conditional risks, and 3 test gaps**. Of these, one is Critical and seven are High (including the conditional transaction risk AUD-013). Severity of a test gap describes assurance work, not an independently demonstrated financial defect.

| Previously Critical/High ID | Disposition after challenge | Demonstrated / not demonstrated |
| --- | --- | --- |
| AUD-001 | Retain Critical, confirmed conditional defect | Blank-secret mutation and nonblank-secret rejection reproduced; deployment secret/reachability unknown. |
| AUD-002 | Retain High, narrow claim | Cross-provider/wrong financial data accepted with an authorized synthetic header; no independent bypass when secret verification is enforced. |
| AUD-003 | Retain High, confirmed | Actual checkout method returns 6.00 and requests 600 cents; no provider charge made. |
| AUD-004 | Retain High, confirmed branch failure | Failure then success and explicit succeeded confirmation both reject transition; actual provider retry scenario not exercised. |
| AUD-005 | Retain High, narrow to late reconciliation | Local cancellation plus late success/refund behavior reproduced. Remove any implication that business policy necessarily requires immediate automatic refund of a paid cancellation. |
| AUD-006 | Retain High, confirmed conditional defect | Admin-created PROCESSING/UNPAID followed by success remains UNPAID; requires authorized admin transition, not a user privilege bypass. |
| AUD-007 | Downgrade to Medium, suspected operational impact | Unmatched-event replay suppression reproduced. Remove claim that ordinary client-confirmed checkout success normally races its own commit: the client receives its secret after checkout returns/commits. Actual early event source remains unproven. |
| AUD-009 | High potential risk; partial-refund/return policy unverified | SHIPPED refund restores all units; DELIVERED is guarded. Controller drops monetary refund fields. A real partial-refund provider delivery and physical-return policy were not tested. |
| AUD-013 | Retain High, suspected transaction consequence | External-call-before-later-error and mail-error-before-event-finalization reproduced; actual database rollback/commit-failure and duplicate delivery remain untested. |
| AUD-014 | **Remove from active findings** | No Hibernate/MySQL stale-entity or overselling reproduction. Pre-read followed by a pessimistic lock alone does not establish stale writes. Retain only an unanswered concurrency verification question. |
| AUD-016 | Downgrade to Medium, suspected provider impact | Identical outgoing keys for separate local caller/order scopes reproduced. No provider collision/rejection or stuck checkout demonstrated. |
| AUD-018 | Downgrade to Medium, policy-dependent risk | Immediate stock/coupon reservation reproduced. Indefinite retention or exhaustion not demonstrated; external cleanup and policy unknown. |
| AUD-021 | Downgrade to Medium, test gap | Static inspection proves the test bypasses full checkout and assertions allow zero sales; no passing/broken database test run claimed. |
| AUD-022 | Downgrade to Medium, test gap | Repository coverage search, not a new exploit finding. |
| AUD-023 | Downgrade to Medium, test gap | Repository coverage search, not proof of all proposed failure scenarios. |

#### Isolation method and evidentiary limits

The executable appendix compiles the current method bodies of OrderServiceImpl, OrderItemServiceImpl, PromoPricingService, and PaymentWebhookController entirely in memory. To avoid annotation-processor/build writes, it mechanically replaces Lombok constructor/logger annotations with equivalent declarations **in memory only**. Supporting model/interface/library classes come from the already cached target/classes and Maven dependencies. No source, test, dependency, database, or build output is written. Failed preliminary compiler setup attempts are not counted as reproductions.

Repositories use Java dynamic proxies backed by maps/sets and the real item service changes a fixture Product. Payment and address collaborators are fakes; mail is a lambda with optional injected failure. The real order/promo/webhook method bodies execute. No Spring context, HTTP server, security filter chain, ORM, transaction interceptor, provider transport, or mail transport starts. Thus “200/401” below are controller ResponseEntity values, not observed HTTP responses. Lock methods are stub lookups, not actual locks; fixture object mutations are not committed database rows.

These checks prove branch behavior with precise fixtures. They do not prove deployed exploitability, actual money movement, database races, transaction rollback, or a provider's delivery semantics. Security/transaction wiring is separately traced from source. A current-source compiler failure or missing cached dependencies invalidates a rerun; a zero JShell process exit alone is insufficient—require ALL_BRANCH_CHECKS_PASSED and no exception/compilation error.

#### AUD-001 — Exact conditional trust-boundary bypass

- **Required conditions:** Reachable PayPal webhook; blank/null configured paypal.webhook.secretKey; fresh event ID; parseable existing PENDING non-COD order; mail/save collaborators succeed. Deployed configuration was not read. No token should be sent: an invalid token can cause JwtFilter to reject before the public route.
- **Execution path:** PublicPathConfig:23 -> SecurityConfig:51-52 permitAll; JwtFilter:55-58 continues with no token -> PaymentWebhookController.handlePayPalEvent:71 skips verification -> :77-89 parses event/order ID -> OrderServiceImpl.handlePayPalWebhookEvent:348 duplicate check, :356 order lock lookup, :362 COD exclusion -> :367 applyPaymentEventTransition -> :539-540 success substring -> :567 valid PENDING->PAID -> :568-574 payment fields/save/mail -> :368 event insert -> controller :90 returns 200.
- **Existing safeguards:** Nonblank secret requires an exact header match (controller :71-74); duplicate IDs return early; unknown IDs do not mutate orders; COD excluded; state transition validated; write lock/transaction present; mail failure prevents successful completion. CORS is not general server-to-server authentication. Stripe signature verification is on a different route and does not guard PayPal.
- **Executed reproduction:** Synthetic PAYMENT.CAPTURE.COMPLETED with custom_id of the fixture, no header, blank secret: controller returns 200; order becomes PAID. Repeat with nonblank secret and absent header: 401; remains PENDING.
- **Reproducibility boundary:** Deterministic isolated controller/service reproduction. Public-route reachability established by source only; no live endpoint or deployed secret tested.
- **Impact/fix:** Unverified payment mutation in the blank-secret configuration. Fail closed and require verified provider/relay authentication; see original fix proposal.

#### AUD-002 — Exact cross-provider/financial-validation omission

- **Required conditions:** Event already passes the configured header check, or AUD-001's blank-secret condition; fresh ID; resource custom_id/invoice maps to a PENDING card/PayPal order. The correct secret is a prerequisite when configured. A synthetic trusted request is not proof an outsider can supply it.
- **Execution path:** Controller :71-89 validates header then extracts only event ID/type, order ID, external ID; amount/currency/merchant are not forwarded -> order service :356 locks supplied order -> :362 rejects only cod -> :367 -> :539 -> :568-570 sets PAID and externalPaymentId. No matching provider intent/capture, amount, or currency comparison occurs.
- **Existing safeguards:** Shared-secret header when nonblank; numeric mapping; existing-order lookup; COD exclusion; deduplication, order lock, allowed transition, and transaction. Normal card checkout's server-side amount protects creation but does not bind this separate PayPal event path.
- **Executed reproduction:** Correct synthetic header; event amount 0.01 EUR; mapped PENDING card order total 27 USD: 200/PAID. Same path targeting COD remains PENDING.
- **Reproducibility boundary:** Acceptance of inconsistent trusted input demonstrated. Real signed/captured PayPal event generation, malicious selection of custom_id, and actual underpayment not demonstrated. This is a separate missing binding check, not a second independently proven authentication bypass.
- **Impact/fix:** Incorrectly mapped trusted events can settle unrelated or financially mismatched orders. Bind provider identity and financial fields before applying payment state.

#### AUD-003 — Exact free-shipping checkout arithmetic

- **Required conditions:** Valid authenticated new checkout; available product at 10.00, quantity 1; shipping price 5.00; active eligible free_shipping promo with usage available; successful fake payment/address collaborators. Requires an accepted promo, not arbitrary client pricing.
- **Execution path:** OrderController.createOrder:33-40 -> createOrder:87-109 normalization, product validation, stock snapshot -> calculatePricing:395 tax=1.00, :396 shipping=5.00, :406 evaluator -> PromoPricingService:107-109 discount=5.00, shipping=0 -> calculatePricing:417-422 total=10+1+0-5=6 -> createOrder:125-130 payment request amount=600.
- **Existing safeguards:** Database product price, stock validation/lock, promo activity/date/user/eligibility/usage checks, HALF_UP rounding, zero floor, server-derived currency. All run before this result and do not correct double subtraction. Existing promo test asserts both intermediate values, not final checkout total.
- **Executed reproduction:** Real checkout/item/promo methods with in-memory repositories return totalAmount=6.00; recording payment fake receives 600 cents. One free shipping benefit should yield 11.00 under the implementation's existing tax rule.
- **Reproducibility boundary:** Final application amount demonstrated; actual Stripe acceptance/charge not attempted. Non-free-shipping tax policy was not judged.
- **Impact/fix:** Undercharge equals original shipping in this fixture. Represent free shipping once in the total composition.

#### AUD-004 — Exact failure-then-success state rejection

- **Required conditions:** Associated PENDING card order; valid distinct failure/success events, with failure applied first; subsequent success for the same intent (or explicit confirmation fake reports succeeded). Failure event must commit in a real deployment for the second transaction to see it. Provider production of that sequence is a separate assumption.
- **Execution path:** Stripe controller :42-47 after signature verification -> service :332-341 mapping/lock -> :544-545 markPaymentFailed -> :597-605 PAYMENT_FAILED/FAILED and stock release. Later success -> :539-540 markPaid -> :567 validateTransition -> :60 empty successor set -> :529 IllegalArgumentException. Explicit confirmation :294-310 checks succeeded, locks, then encounters the same transition.
- **Existing safeguards:** Verified Stripe signature, intent mapping, duplicate IDs, order lock/transaction; failure restores stock only once; success after already-PAID is handled idempotently. The rejected success is not recorded processed because :342 is never reached, allowing retry but not resolving its invalid source state.
- **Executed reproduction:** Failure changes fixture stock 10->12 for two reserved units. Success throws PAYMENT_FAILED -> PAID; explicit succeeded confirmation also throws; success event absent from fixture processed set.
- **Reproducibility boundary:** Deterministic sequential method behavior demonstrated; no live retry/delivery sequence or DB commit tested. A stale failure delivered after an already-PAID success is ignored, so this is not a claim that every event order fails.
- **Impact/fix:** A later authoritative success has no supported reconciliation from PAYMENT_FAILED. Model attempts and released inventory explicitly, rather than treating every failed attempt as an irreversible order outcome.

#### AUD-005 — Exact local cancellation / late payment mismatch

- **Required conditions:** Owner/admin cancels PENDING card order with an associated still-live intent; a genuine success later arrives (e.g. an external confirmation already underway). The fake supplies this event; provider completion after local cancellation was not tested. A paid cancellation's refund policy is unspecified.
- **Execution path:** OrderController.cancelOrder:92-100 -> loadAuthorizedOrderForUpdate:380-385 -> cancelOrder:235-247 validates cancellable state, sets CANCELLED, restores stock, saves. No payment collaborator called. Later signed Stripe success -> markPaid:567 -> transition map :61 rejects CANCELLED->PAID. Refund event -> markRefunded:628-642 excludes CANCELLED -> handleStripeWebhookEvent:342 nevertheless marks that refund processed.
- **Existing safeguards:** Ownership/admin check, cancellable-state allowlist, pessimistic order lock, transactional local updates, stockReleased guard, signature/intent mapping on subsequent Stripe events. Local cancellation does not itself make the provider intent unpayable. Explicit confirmation with paymentStatus already PAID returns early and is not a general cancelled-order reconciliation tool.
- **Executed reproduction:** Authorized owner cancellation restores two units. Synthetic later success throws; following refund is processed while order stays CANCELLED; recording payment fake sees no provider operations.
- **Reproducibility boundary:** Local inconsistency demonstrated. No actual provider settlement/refund or unauthorized cancellation reproduced. Remove prior implication that absence of immediate auto-refund is independently a confirmed policy violation.
- **Impact/fix:** Late financial outcomes cannot reconcile the cancelled order through these methods. Coordinate cancellation and payment reconciliation independently from fulfillment state.

#### AUD-006 — Exact paid update swallowed after authorized processing

- **Required conditions:** ADMIN uses status route to advance PENDING/UNPAID online order to PROCESSING; genuine success arrives later; mail succeeds. Ordinary USER cannot perform this admin step.
- **Execution path:** @PreAuthorize ADMIN at OrderController:75 -> updateOrderStatusByAdmin:210 only disallows payment target statuses -> :217 allows PENDING->PROCESSING from :55 -> saves without payment change. Success -> markPaid:560-565 sees PROCESSING, sends/checks email, returns before :569. Explicit confirmation :277-282 returns from fulfillment status without retrievePaymentIntent.
- **Existing safeguards:** ADMIN role, order lock, transition validation, signature/mapping/event dedup for Stripe, email-sent flag. COD legitimately uses unpaid PROCESSING, but there is no payment-method condition on the same admin transition.
- **Executed reproduction:** Admin service operation then Stripe success leaves paymentStatus=UNPAID; explicit confirmation makes zero payment retrieval calls.
- **Reproducibility boundary:** Branch inconsistency reproduced; method-role wiring inspected, not exercised through HTTP. No customer privilege escalation or real unpaid shipment claimed.
- **Impact/fix:** Monetary state is lost after an allowed fulfillment transition. Apply monetary success independent of fulfillment or constrain unpaid online processing according to policy.

#### AUD-009 — Exact refund amount loss and restock conditions

- **Required conditions:** Supported refund event passes provider verification and maps to an existing order in PAID, PROCESSING, SHIPPED, or DELIVERED. To claim a partial-refund incident, the provider must actually emit this event for a partial refund; this audit did not exercise that provider behavior. Physical-return policy is unknown.
- **Execution path:** Stripe controller :48-51 reads charge identifiers only; :56 calls service with no amount/full-refund flag -> :341 -> :554-555 -> markRefunded:628-639 sets REFUNDED; for every state except DELIVERED, releaseStockIfNeeded:649-652 restores each whole item quantity. Equivalent PayPal handler also has no monetary refund input.
- **Existing safeguards:** Signature/secret verification as configured, intent/order mapping, unique-event check, write lock/transaction, source-state allowlist, stockReleased flag, REFUNDED early return. Crucially **DELIVERED orders are not restocked**; cancelled/refunded orders are not repeatedly restocked. This replaces the earlier overbroad “every refund/restock” phrasing.
- **Executed reproduction:** SHIPPED order with two units changes to REFUNDED and fixture stock 10->12. DELIVERED fixture remains stock=10. Service cannot observe whether a refund is partial because the amount is absent from its arguments.
- **Reproducibility boundary:** Whole-quantity behavior and delivered guard demonstrated. Actual partial-refund payload deserialization, shipment return, and provider delivery were not run. There is no claim that a delivered refund duplicates stock.
- **Impact/fix:** The implemented refund path cannot distinguish partial monetary adjustment or physical return and can restock in-transit goods. Track refund and inventory return evidence separately.

#### AUD-013 — Exact external-side-effect ordering; transactional consequence remains conditional

- **Required conditions:** Card creation call returns successfully, then address save/flush/commit fails; or mail sends successfully then the DB commit fails; or mail throws while handling an already-settled payment. A later failure is required; the normal successful path is not inconsistent.
- **Execution path:** @Transactional createOrder:80 -> database order/items/totals -> :125-135 external intent creation -> :143 address persistence. Separately webhook @Transactional :322 -> markPaid:568-574 -> sendConfirmationEmailIfNeeded:581-584 mail exception rethrown as runtime error -> :342 processed-event insert not reached. Mail-sent flag is only persisted after send (:586-590).
- **Existing safeguards:** Runtime errors cause rollback under Spring transaction interception; Stripe request has an idempotency key; order/email flags prevent sequential resends after a successful commit; event record and state share one DB transaction. These controls do not roll back a previously completed provider/mail operation. Signature failures never reach mutation. Provider creation failure before a response also throws and aborts local checkout.
- **Executed reproduction:** Recording fake confirms one intent creation before injected address exception. Injected MessagingException propagates as IllegalStateException and prevents event finalization. No mail transport or DB was used.
- **Not reproduced:** Spring-intercepted rollback, flush/commit failure, surviving remote intent, duplicate SMTP delivery, provider compensation, crash recovery. Fixture map changes are deliberately not treated as rollback evidence.
- **Disposition/impact/fix:** Retain High **suspected**, not confirmed transaction failure: the code ordering and exception path are demonstrated, but production consequence requires fault/transaction verification. Use durable recoverable payment/notification operations and independently retry email.

#### All formerly High items no longer classified High

- **AUD-007 (Medium risk):** Required unmatched ID and fresh event -> :332-336 writes processed marker; same event after mapping -> :324 returns before lookup. Reproduced. Safeguards include signature, key uniqueness, and normal client secret delivery after checkout completion. Reconciliation failure for an ordinary successful checkout is not established; other operational event sources must be identified.
- **AUD-014 (removed):** Early product/order reads and later lock queries exist, but neither cached object freshness nor a lost update was measured under Hibernate/MySQL. Existing pessimistic locks, same-transaction item updates, order lock, and uniqueness constraints must be tested rather than presumed ineffective. No overselling finding remains.
- **AUD-016 (Medium risk):** Separate users with separate persisted order IDs and the same supplied key reach createOrder:125-130 and PaymentServiceImpl:40-43 with the same outgoing key. Source-derived request mismatch and recording-fake arguments are demonstrated; provider idempotency rejection/recovery is not. Local user+key uniqueness and existing-checkout return are safeguards. Provider collision severity was unsupported.
- **AUD-018 (Medium risk):** createOrder:107-120 reserves before payment, confirmed with stock 10->9 and promo count 0->1. No scheduler in inspected backend source; explicit cancellation and failure/refund stock-release paths exist. No indefinite hold, stock exhaustion, absent external job, or required coupon-restoration policy was established.
- **AUD-021–023 (Medium test gaps):** Relevant test file/line evidence remains in their table. The concurrency test goes directly to saveOrderItems and accepts zero successes in its final assertions; Mockito service tests do cover isolated outcomes, owner denial, sequential duplicate handling, COD lifecycle, and promo constraints. Missing full-path/concurrency/HTTP/fault tests are assurance gaps, not additional high-severity runtime defects. Reproduce the coverage assessment by the repository searches in section 4, not by running database tests against shared data.


### Latest verification outcome

The corrected embedded runner was executed during this re-examination. All 11 isolated branch-check groups completed and printed ALL_BRANCH_CHECKS_PASSED. Guards verified include nonblank PayPal secret rejecting missing header, COD exclusion, and no stock restoration for DELIVERED refunds. All four audited method-source hashes still match the recorded values. The runner previously selected the wrong Java block because its marker strings also appeared inside the runner itself; it now selects standalone marker lines. This was a documentation-runner correction, not an application fix.

No Spring/ORM/database transaction, real HTTP filter chain, real provider, or SMTP behavior was exercised. Other findings retain source-based evidence and explicit reproduction conditions rather than new runtime claims.

## 4. Reproduction and verification plan — safe, local/test only

The original verification plan is retained below. Isolated checks actually executed during re-examination are documented in the preceding re-examination subsection; database/provider/HTTP cases remain proposed. Use fake provider/mail adapters or Mockito; do not send test payloads to a deployed/shared service. Database cases require a separately provisioned disposable MySQL database. Do not run the full suite against configured shared data or start this application merely to inspect schema: baseline/startup code can write data.

| Findings | Local verification procedure and observable result |
| --- | --- |
| AUD-001 | Instantiate webhook controller with blank PayPal secret and a mocked order service. Submit a synthetic completed event without a header. Verify delegation. With real service plus in-memory mocked repositories and fake mail, verify PENDING becomes PAID. Repeat with a nonblank secret to verify the conditional guard. |
| AUD-002 | Feed handlePayPalWebhookEvent a PENDING card fixture and completed event; verify PAID. Controller fixture with mismatched financial JSON should delegate the same identifiers because those fields are not read. No real PayPal request is needed. |
| AUD-003 | Evaluate free_shipping for subtotal=10, shipping=5, then apply actual quote/checkout formula. Assert observed total=6 with tax=1; compare business expectation=11. Include final cart total, not only evaluator fields. |
| AUD-004 | Apply failed event then success to the same mocked managed order. Observe stock restoration once, then invalid transition and persistent FAILED state. Repeat with fake retrieved succeeded intent via explicit confirmation. |
| AUD-005 | Cancel a pending card fixture, then apply success and refund events. Observe rejected success and unchanged CANCELLED state for refund. Verify no provider cancel/refund invocation exists. Include paid cancellation separately. |
| AUD-006 | Admin-update PENDING/UNPAID card order to PROCESSING; apply success and invoke confirmation. Observe UNPAID remains and confirmation does not retrieve the intent. |
| AUD-007 | First event: repository returns no intent mapping and event insert is recorded. Then expose mapping and return duplicate=true for replay. Verify no order update on replay. |
| AUD-008 | Use existing checkout fixture but product quantity=0 for request quantity=1. Verify stock exception occurs before key lookup returns existing result. |
| AUD-009 | Use a multi-item PAID fixture and synthetic refund event representing less than the order total. Observe all item quantities restored and order REFUNDED, with no amount comparison. Repeat SHIPPED to document physical-return behavior. |
| AUD-010–012 | Exercise real address service with mocked repositories: matching address + null isDefault throws; existing nondefault + true does not clear old default. For null equality, use disposable SQL fixtures to verify repeated identical null-apartment address lookup misses. |
| AUD-013 | Fake Stripe succeeds; make later address save or transaction commit fail. Verify external call survives failed DB operation. Fake mail succeeds, then fail commit and retry; count delivery attempts. Fake mail failure during paid event should roll back event/order writes in a real transaction. |
| AUD-014 (removed; open question only) | Coordinate two full checkout transactions with barriers after early product reads and before locked decrement. Initial stock=1; require committed items sold<=1 and quantity conservation. Also pause confirmation/event after initial order read while cancellation commits; inspect actual SQL, entity state, and email count. |
| AUD-015 | Two valid multi-product orders with reverse item order; synchronize after first lock and record deadlock/timeout handling. Never use shared inventory. |
| AUD-016–017 | Use a recording fake provider to inspect keys and metadata for different users sharing a key and post-rollback retry. Use disposable DB for duplicate checkout/event races; assert one durable operation and deterministic replay response, not just one unique row. Provider-specific behavior requires a separately approved sandbox exercise. |
| AUD-018–020 | Seed disposable pending reservations, failed/cancelled promo orders, historical fulfilled online UNPAID rows, and boundary address inputs. Inspect release/backfill/validation outcomes with all integrations fake. Do not run migration/backfill against real data. |
| AUD-021–023 | Repair fixtures and assertions only in a later authorized implementation phase; add the missing trust-boundary, order-sequence, and transaction tests described above. |

Verification performed: source/evidence/test/constraint review and the isolated in-memory branch checks recorded above. No database, deployed HTTP, real provider, or mail-transport reproduction is claimed.

## 5. Open questions and recommended priorities

### Unknowns

1. Is PayPal webhook traffic direct from the provider or forwarded by a relay that injects the custom secret? Is an empty secret possible in any reachable deployment? Secret values were not inspected or recorded.
2. Where are PayPal orders/captures created, what trusted amount/currency/merchant data exists, and how are provider identifiers associated with local orders? No outgoing PayPal client appeared in the reviewed backend flow.
3. Are partial refunds supported? Does a monetary refund imply physical return, and when may SHIPPED stock become saleable again?
4. May online orders enter PROCESSING before payment? Is paid cancellation supposed to trigger automatic refund or a separate staff workflow?
5. What do paymentStatus=PAID and REFUNDED mean to clients: historical collection or current net settlement? How should cancelled/partially refunded orders display settlement?
6. What are reservation TTL, abandoned-checkout cleanup, and coupon restoration policies? Are there external scheduled reconciliation jobs outside this repository?
7. What are the deployed MySQL isolation, Hibernate refresh behavior, lock timeouts, and schema definitions? No runtime database was accessed.
8. Are provider retries/timeouts configured outside this code? What Stripe event ordering/retry histories occur in deployment? No live provider behavior was tested.
9. Which address country format is intended, and how are optional address/default fields supplied by clients?
10. Is the standalone admin PaymentIntent endpoint actively used, and how are those intents reconciled to orders?

### Suggested priority order

1. **Payment trust:** AUD-001 and AUD-002; add AUD-022 trust-boundary tests alongside any selected fixes.
2. **Financial/inventory reconciliation:** AUD-003 through AUD-006 and AUD-009; pair with AUD-023 event/final-total coverage and AUD-013 fault verification.
3. **Concurrency assurance:** use a full-checkout database test to answer the removed AUD-014 question and strengthen AUD-021 coverage. No overselling defect is established; investigate AUD-015 through AUD-017 according to reproduced outcomes.
4. **Recovery and input consistency:** AUD-008, AUD-010 through AUD-012, and policy/schema risks AUD-018 through AUD-020.

No fixes have been implemented or approved. Wait for the user to select findings before starting the next stage.

## Appendix — executed isolated reproduction harness

Run the Python runner below from the repository root with Python 3 and JDK 21. It requires the existing cached classpath XML, target model/interface classes, and cached Maven jars. It performs no Maven build, application startup, database operation, provider call, or file output. The Java fixture and compiler are read from this report. This is diagnostic documentation, not an installed regression test.

<!-- audit-runner -->
```python
from pathlib import Path
import base64, json, subprocess, xml.etree.ElementTree as ET
report = Path("docs/agent-runs/order-audit-001/audit-report.md").read_text()
def block(marker, language):
    return report.split("\n" + marker + "\n", 1)[1].split("```" + language + "\n", 1)[1].split("\n```", 1)[0]
harness = block("<!-- audit-java-fixture -->", "java")
compiler = block("<!-- audit-java-compiler -->", "java")
xml = ET.parse("target/surefire-reports/TEST-com.duytoan.imajicoffee.imaji_coffee_be.order.OrderServiceImplTest.xml")
cp = next(p.attrib["value"] for p in xml.getroot().find("properties") if p.attrib["name"] == "java.class.path")
source = compiler.replace("HARNESS_BASE64", base64.b64encode(harness.encode()).decode())
snippet = source.replace("public class MemoryAudit", "class MemoryAudit") + "\nSystem.setProperty(\"audit.cp\"," + json.dumps(cp) + ");\nMemoryAudit.main(new String[0]);\n"
result = subprocess.run(["jshell", "--class-path", cp, "--execution", "local", "-"], input=snippet, text=True, capture_output=True, timeout=55)
print(result.stdout)
print(result.stderr)
if "ALL_BRANCH_CHECKS_PASSED" not in result.stdout or "Exception " in result.stdout:
    raise SystemExit("Branch checks did not complete successfully")
```

<!-- audit-java-fixture -->
```java

import java.lang.reflect.*;
import java.math.*;
import java.time.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.*;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.*;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.*;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.*;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.*;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.*;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.payment.*;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.*;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.*;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.*;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.*;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.payment.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.impl.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.payment.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.*;
import com.duytoan.imajicoffee.imaji_coffee_be.controller.payment.*;
public class AuditCheck {
 interface Call {Object invoke(String name,Object[] args) throws Throwable;}
 static <T>T proxy(Class<T> c,Call call){return c.cast(Proxy.newProxyInstance(c.getClassLoader(),new Class[]{c},(p,m,a)->{if(m.getName().equals("toString"))return c.getSimpleName();return call.invoke(m.getName(),a);}));}
 static void check(boolean b,String label){if(!b)throw new AssertionError(label);}
 static void field(Object o,String n,Object v)throws Exception{var f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static class F {
  Map<Long,Order> orders=new HashMap<>(); Set<String> events=new HashSet<>(); List<PaymentIntentRequestDto> paymentCalls=new ArrayList<>(); Product product=new Product(); User user=new User(); Ship ship=new Ship(); Promo promo=new Promo(); int mail=0,retrieve=0; long id=1; boolean failAddress=false,failMail=false;
  OrderRepository repo; PaymentWebhookEventRepository er; ProductRepository pr; PromoRepository promos; PromoUsageRepository usages; OrderServiceImpl svc; OrderItemServiceImpl items; IPaymentService payment;
  F(){
   product.setProductId(101L);product.setName("Audit Coffee");product.setPrice(new BigDecimal("10.00"));product.setQuantity(10);product.setIsAvailableAtWeb(true);product.setCategory("coffee_baverage");
   var img=new ProductImage();img.setIsMain(true);img.setImageUrl("audit-image");product.setProductImages(new LinkedHashSet<>(List.of(img)));
   user.setUserId(10L);user.setUsername("audit");user.setEmail("audit@example.invalid");
   ship.setMethodId(1L);ship.setMethodName("Standard");ship.setExpectedArrival("3 days");ship.setPrice(new BigDecimal("5.00"));
   promo.setPromoId(1L);promo.setCode("FREE");promo.setIsActive(true);promo.setStatus("ACTIVE");promo.setDiscountType("free_shipping");promo.setDiscountValue(BigDecimal.ZERO);
   repo=proxy(OrderRepository.class,(n,a)->switch(n){
    case "save"->{Order o=(Order)a[0];if(o.getOrderId()==null)o.setOrderId(id++);orders.put(o.getOrderId(),o);yield o;}
    case "findByOrderIdForUpdate","findById"->Optional.ofNullable(orders.get((Long)a[0]));
    case "findByOrderIdAndUserId"->Optional.ofNullable(orders.get((Long)a[0])).filter(o->Objects.equals(o.getUserId(),a[1]));
    case "findByPaymentIntentId"->orders.values().stream().filter(o->Objects.equals(o.getPaymentIntentId(),a[0])).findFirst();
    case "findByUserIdAndCheckoutRequestId"->orders.values().stream().filter(o->Objects.equals(o.getUserId(),a[0])&&Objects.equals(o.getCheckoutRequestId(),a[1])).findFirst();
    default->throw new UnsupportedOperationException(n);
   });
   er=proxy(PaymentWebhookEventRepository.class,(n,a)->switch(n){case "existsByProviderAndEventId"->events.contains(a[0]+":"+a[1]);case "save"->{var e=(PaymentWebhookEvent)a[0];events.add(e.getProvider()+":"+e.getEventId());yield e;}default->throw new UnsupportedOperationException(n);});
   pr=proxy(ProductRepository.class,(n,a)->switch(n){case "findById","findByProductIdForUpdate"->Optional.of(product);case "save"->a[0];default->throw new UnsupportedOperationException(n);});
   promos=proxy(PromoRepository.class,(n,a)->Optional.of(promo));
   usages=proxy(PromoUsageRepository.class,(n,a)->switch(n){case "countByPromo_PromoIdAndUserId"->0L;case "save"->a[0];case "findByOrderId"->Optional.empty();default->null;});
   items=new OrderItemServiceImpl(proxy(OrderItemRepository.class,(n,a)->{if(n.equals("saveAll"))return a[0];throw new UnsupportedOperationException(n);}),pr);
   payment=proxy(IPaymentService.class,(n,a)->switch(n){case "validateCardPaymentConfiguration"->null;case "createPaymentIntent"->{paymentCalls.add((PaymentIntentRequestDto)a[0]);yield new PaymentIntentResponseDto("pi_"+paymentCalls.size(),"cs_audit");}case "getClientSecret"->"cs_audit";case "retrievePaymentIntent"->{retrieve++;var pi=new com.stripe.model.PaymentIntent();pi.setStatus("succeeded");pi.setLatestCharge("ch_audit");yield pi;}default->throw new UnsupportedOperationException(n);});
   IMailService ms=o->{mail++;if(failMail)throw new jakarta.mail.MessagingException("injected mail failure");};
   svc=new OrderServiceImpl(repo,new ObjectMapper(),items,payment,proxy(ShipRepository.class,(n,a)->Optional.of(ship)),proxy(UserRepository.class,(n,a)->Optional.of(user)),new AddressServiceImpl(null,null){public Address saveAddressForOder(AddressDto a,Long u){if(failAddress)throw new IllegalStateException("injected address failure");return null;}},ms,promos,usages,er,pr,new PromoPricingService(usages));
  }
  Order seed(String method,OrderStatus state){var o=new Order();o.setOrderId(id++);o.setUserId(10L);o.setPaymentMethod(method);o.setStatus(state);o.setCurrency("USD");o.setTotalAmount(new BigDecimal("27"));o.setTaxAmount(new BigDecimal("2"));o.setShippingAmount(new BigDecimal("5"));o.setDiscountAmount(BigDecimal.ZERO);o.setEmail("audit@example.invalid");o.setPaymentIntentId("pi_seed");o.setOrderItems(new LinkedHashSet<>(List.of(item())));orders.put(o.getOrderId(),o);return o;}
  OrderItem item(){var i=new OrderItem();i.setProductId(101L);i.setPrice(new BigDecimal("10"));i.setQuantity(2);return i;}
  OrderRequestDto request(String key,String coupon){return new OrderRequestDto("card",1L,new AddressDto(null,10L,"Audit User","US","CA","SF","Audit Street","12345","1","+14155550123",false),coupon,key,List.of(new OrderItemDto(101L,1)));}
  PaymentWebhookController controller(String secret)throws Exception{var c=new PaymentWebhookController(svc,new ObjectMapper());field(c,"paypalWebhookSecret",secret);return c;}
 }
 static String event(String id,Long order){return "{\"id\":\""+id+"\",\"event_type\":\"PAYMENT.CAPTURE.COMPLETED\",\"resource\":{\"id\":\"capture_audit\",\"custom_id\":\""+order+"\",\"amount\":{\"value\":\"0.01\",\"currency_code\":\"EUR\"}}}";}
 public static void main(String[] args)throws Exception{
  {var f=new F();var o=f.seed("paypal",OrderStatus.PENDING);var r=f.controller("").handlePayPalEvent(event("blank",o.getOrderId()),null);check(r.getStatusCode().value()==200&&o.getPaymentStatus()==PaymentStatus.PAID,"001");var g=new F();var q=g.seed("paypal",OrderStatus.PENDING);var s=g.controller("audit-secret").handlePayPalEvent(event("guard",q.getOrderId()),null);check(s.getStatusCode().value()==401&&q.getStatus()==OrderStatus.PENDING,"001 guard");System.out.println("AUD-001 PASS blank secret:200/PAID; configured secret + missing header:401/PENDING");}
  {var f=new F();var o=f.seed("card",OrderStatus.PENDING);var r=f.controller("audit-secret").handlePayPalEvent(event("cross",o.getOrderId()),"audit-secret");check(r.getStatusCode().value()==200&&o.getPaymentStatus()==PaymentStatus.PAID,"002");var g=new F();var q=g.seed("cod",OrderStatus.PENDING);g.controller("audit-secret").handlePayPalEvent(event("cod",q.getOrderId()),"audit-secret");check(q.getStatus()==OrderStatus.PENDING,"002 cod");System.out.println("AUD-002 PASS trusted-header event 0.01 EUR settles card order 27 USD; COD excluded");}
  {var f=new F();var r=f.svc.createOrder(f.request("free","FREE"),10L);check(r.totalAmount().compareTo(new BigDecimal("6.00"))==0,"003");check(f.paymentCalls.get(0).amount()==600,"003 intent");System.out.println("AUD-003 PASS real checkout total=6.00, fake intent amount=600; subtotal=10 tax=1 original shipping=5");}
  {var f=new F();var o=f.seed("card",OrderStatus.PENDING);f.svc.handleStripeWebhookEvent("failure","payment_intent.payment_failed","pi_seed","ch");check(o.getStatus()==OrderStatus.PAYMENT_FAILED&&f.product.getQuantity()==12,"004 fail");try{f.svc.handleStripeWebhookEvent("success","payment_intent.succeeded","pi_seed","ch");throw new AssertionError("004 no throw");}catch(IllegalArgumentException e){check(e.getMessage().contains("PAYMENT_FAILED -> PAID"),"004 msg");}try{f.svc.confirmStripePayment(o.getOrderId(),10L,false);throw new AssertionError("004 confirm");}catch(IllegalArgumentException expected){}check(!f.events.contains("STRIPE:success"),"004 event");System.out.println("AUD-004 PASS failure restores 2; subsequent success and succeeded confirmation both throw; success event not finalized");}
  {var f=new F();var o=f.seed("card",OrderStatus.PENDING);f.svc.cancelOrder(o.getOrderId(),10L,false,"audit");try{f.svc.handleStripeWebhookEvent("late","payment_intent.succeeded","pi_seed","ch");throw new AssertionError("005");}catch(IllegalArgumentException expected){}f.svc.handleStripeWebhookEvent("refund","charge.refunded","pi_seed","ch");check(o.getStatus()==OrderStatus.CANCELLED&&f.events.contains("STRIPE:refund")&&f.product.getQuantity()==12,"005 refund");System.out.println("AUD-005 PASS owner cancel restores 2; late success throws; refund acknowledged with CANCELLED unchanged; no provider call");}
  {var f=new F();var o=f.seed("card",OrderStatus.PENDING);f.svc.updateOrderStatusByAdmin(o.getOrderId(),OrderStatus.PROCESSING,"admin");f.svc.handleStripeWebhookEvent("paid","payment_intent.succeeded","pi_seed","ch");f.svc.confirmStripePayment(o.getOrderId(),10L,false);check(o.getPaymentStatus()==PaymentStatus.UNPAID&&f.retrieve==0,"006");System.out.println("AUD-006 PASS admin PROCESSING then success leaves UNPAID; explicit confirmation retrieves=0");}
  {var f=new F();f.svc.handleStripeWebhookEvent("early","payment_intent.succeeded","pi_seed","ch");var o=f.seed("card",OrderStatus.PENDING);f.svc.handleStripeWebhookEvent("early","payment_intent.succeeded","pi_seed","ch");check(o.getStatus()==OrderStatus.PENDING&&f.events.contains("STRIPE:early"),"007");System.out.println("AUD-007 PASS unmatched event stored; same event after mapping leaves PENDING");}
  {var f=new F();var o=f.seed("card",OrderStatus.SHIPPED);o.setPaymentStatus(PaymentStatus.PAID);f.svc.handleStripeWebhookEvent("refund","charge.refunded","pi_seed","ch");check(o.getStatus()==OrderStatus.REFUNDED&&f.product.getQuantity()==12,"009");var g=new F();var q=g.seed("card",OrderStatus.DELIVERED);g.svc.handleStripeWebhookEvent("refund2","charge.refunded","pi_seed","ch");check(g.product.getQuantity()==10,"009 delivered");System.out.println("AUD-009 PASS SHIPPED refund restores all 2; DELIVERED refund restores 0; amount unavailable to service");}
  {var f=new F();f.failAddress=true;try{f.svc.createOrder(f.request("fault",null),10L);throw new AssertionError("013");}catch(IllegalStateException expected){}check(f.paymentCalls.size()==1,"013 call");var g=new F();g.failMail=true;var o=g.seed("card",OrderStatus.PENDING);try{g.svc.handleStripeWebhookEvent("mailfail","payment_intent.succeeded","pi_seed","ch");throw new AssertionError("013 mail");}catch(IllegalStateException expected){}check(!g.events.contains("STRIPE:mailfail"),"013 event");System.out.println("AUD-013 PASS fake intent created before injected address exception; mail exception prevents event finalization (DB rollback NOT tested)");}
  {var f=new F();f.svc.createOrder(f.request("shared",null),10L);f.user.setUserId(20L);f.svc.createOrder(f.request("shared",null),20L);check(f.paymentCalls.size()==2&&f.paymentCalls.get(0).idempotencyKey().equals(f.paymentCalls.get(1).idempotencyKey())&&!f.paymentCalls.get(0).orderId().equals(f.paymentCalls.get(1).orderId()),"016");System.out.println("AUD-016 PASS distinct caller scopes send identical raw keys with different order IDs; provider response NOT tested");}
  {var f=new F();f.svc.createOrder(f.request("abandon","FREE"),10L);check(f.product.getQuantity()==9&&f.promo.getUsageCount()==1,"018");System.out.println("AUD-018 PASS checkout without payment success reserves 1 stock and 1 promo use; expiry policy NOT tested");}
  System.out.println("ALL_BRANCH_CHECKS_PASSED; no Spring context, DB, network, or mail transport");
 }
}

```

<!-- audit-java-compiler -->
```java

import javax.tools.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class MemoryAudit {
 static Map<String,ByteArrayOutputStream> bytes=new HashMap<>();
 public static void main(String[] args) throws Exception {
  var compiler=ToolProvider.getSystemJavaCompiler();
  var diag=new DiagnosticCollector<JavaFileObject>();
  var standard=compiler.getStandardFileManager(diag,null,null);
  var manager=new ForwardingJavaFileManager<StandardJavaFileManager>(standard){
   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location l,String n,JavaFileObject.Kind k,FileObject sibling){
    var b=new ByteArrayOutputStream();bytes.put(n,b);
    return new SimpleJavaFileObject(URI.create("mem:///"+n.replace('.','/')+k.extension),k){
     public OutputStream openOutputStream(){return b;}
    };
   }
  };
  var root=Path.of("src/main/java/com/duytoan/imajicoffee/imaji_coffee_be");
  var files=List.of(root.resolve("services/order/impl/OrderServiceImpl.java"),root.resolve("services/order/impl/OrderItemServiceImpl.java"),root.resolve("services/promo/PromoPricingService.java"),root.resolve("controller/payment/PaymentWebhookController.java"));
  var units=new ArrayList<JavaFileObject>();

  for(var file:files){
   String text=Files.readString(file);String name=file.getFileName().toString().replace(".java","");
   var matcher=java.util.regex.Pattern.compile("private final ([A-Za-z0-9_<>]+) ([A-Za-z0-9_]+);").matcher(text);
   var params=new ArrayList<String>();var assigns=new StringBuilder();
   while(matcher.find()){params.add(matcher.group(1)+" "+matcher.group(2));assigns.append("this."+matcher.group(2)+"="+matcher.group(2)+";");}
   String ctor="public "+name+"("+String.join(",",params)+"){"+assigns+"}";
   if(text.contains("@Slf4j"))ctor+="private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger("+name+".class);";
   text=text.replace("@RequiredArgsConstructor","").replace("@Slf4j","").replace("public class "+name+(name.equals("OrderServiceImpl")?" implements IOrderService":name.equals("OrderItemServiceImpl")?" implements IOrderItemService":"")+" {","public class "+name+(name.equals("OrderServiceImpl")?" implements IOrderService":name.equals("OrderItemServiceImpl")?" implements IOrderItemService":"")+" {"+ctor);
   String source=text;
   units.add(new SimpleJavaFileObject(file.toUri(),JavaFileObject.Kind.SOURCE){public CharSequence getCharContent(boolean ignore){return source;}});
  }

  String extra=new String(Base64.getDecoder().decode("HARNESS_BASE64"),java.nio.charset.StandardCharsets.UTF_8);
  units.add(new SimpleJavaFileObject(URI.create("string:///AuditCheck.java"),JavaFileObject.Kind.SOURCE){public CharSequence getCharContent(boolean ignore){return extra;}});
  boolean ok=compiler.getTask(null,manager,diag,List.of("-classpath",System.getProperty("audit.cp"),"-encoding","UTF-8","-proc:none"),null,units).call();
  if(!ok){diag.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).forEach(System.out::println);throw new IllegalStateException("compile failed");}
  System.out.println("CURRENT_SOURCE_COMPILED_IN_MEMORY classes="+bytes.size());
  var dependencyUrls=new ArrayList<URL>();for(String entry:System.getProperty("audit.cp").split(File.pathSeparator))if(!entry.isEmpty())dependencyUrls.add(Path.of(entry).toUri().toURL());var dependencyLoader=new URLClassLoader(dependencyUrls.toArray(URL[]::new),ClassLoader.getPlatformClassLoader());
  var loader=new ClassLoader(dependencyLoader){
   protected synchronized Class<?> loadClass(String n,boolean resolve)throws ClassNotFoundException{
    if(bytes.containsKey(n)){var c=findLoadedClass(n);if(c==null){var b=bytes.get(n).toByteArray();c=defineClass(n,b,0,b.length);}if(resolve)resolveClass(c);return c;}
    return super.loadClass(n,resolve);
   }
  };
  try {loader.loadClass("AuditCheck").getMethod("main",String[].class).invoke(null,(Object)new String[0]);}
  catch(java.lang.reflect.InvocationTargetException e){throw new RuntimeException(e.getCause());}
 }
}

```

### Current audited source SHA-256

These hashes identify the source bodies used by the isolated checks; they do not assert that every cached supporting class matches current source.

- `J/services/order/impl/OrderServiceImpl.java`: `65736de91e131399efcc8d0dbe98af604c51c493b3c9055c9a181394e09cc43b`
- `J/services/order/impl/OrderItemServiceImpl.java`: `8e0959e7d97750097e9e1a0855622c8bfac9ab0fed78505b92a29d91125dff78`
- `J/services/promo/PromoPricingService.java`: `2cce1f82b384bc9e2eb267406424cbcdb2ed6f6da09e8441c2ce5914b1cfecec`
- `J/controller/payment/PaymentWebhookController.java`: `d6ce61845456a9a06be4427a7b4136c3075b2ae77da2d21733818c4d7dcb9129`
