# Imaji Coffee — Project Review and Roadmap

This document records the current project assessment and recommended next steps. It supersedes the review dated 2026-07-23: that review's Priority 0 items (authorization, checkout price integrity, webhook idempotency, WebSocket auth, CORS) have since been implemented and are verified fixed below. This update is based on a fresh read of the code on 2026-08-02, with file:line evidence for every claim.

No application code is changed by this document.

## 1. Current project overview

Imaji Coffee is a full-stack coffee e-commerce application with:

- React, TypeScript, Vite, HeroUI, Tailwind CSS, Redux Toolkit (RTK Query) frontend
- Spring Boot, Java 17, Spring Data JPA, Spring Security backend
- MySQL database
- Product catalog, search, categories, promotions, cart, shipping, and checkout
- Stripe and PayPal payment flows
- JWT authentication (cookie-based), role-based access, Google OAuth
- Customer accounts, addresses, order history, news, events, spaces, FAQs
- Customer-admin real-time chat over WebSockets/STOMP
- Caffeine caching, email support, Actuator, Docker, integration tests

The security and checkout foundation is now in materially better shape than the previous review found it. Remaining work is concentrated in operational hygiene (migrations, repo cleanliness), pagination completeness, and frontend consistency/test coverage.

## 2. Resolved since the last review

These items were flagged Priority 0 previously and are now fixed, with evidence:

- **CORS is environment-driven**, not hardcoded to localhost — `imajicoffee.cors.allowed-origins` property, applied consistently in `SecurityConfig.java`, `CorsConfig.java`, and `WebSocketConfig.java`.
- **Endpoint authorization is enforced**: `/api/v1/admin/**` requires `ROLE_ADMIN` (`SecurityConfig.java:55-56`); ownership is checked in the service layer for orders (`OrderServiceImpl.loadAuthorizedOrder`, lines 371-387), addresses (`AddressServiceImpl`, lines 126, 155), chat (`ChatRestController`, multiple endpoints), and carts (userId always derives from the auth token, never the request body).
- **Checkout price integrity**: totals are recalculated server-side from persisted `OrderItem` prices in `OrderServiceImpl.calculatePricing` (lines 389-424); the Stripe PaymentIntent amount uses that server total, not client input.
- **Stripe webhook is authoritative and idempotent**: signature is verified via `Webhook.constructEvent` (`PaymentWebhookController.java:37`), and `isWebhookDuplicate`/`markWebhookProcessed` (`OrderServiceImpl.java:324-369`) prevent double-processing.
- **WebSocket auth is active**: `ChatStompAuthChannelInterceptor` binds a JWT-derived principal on CONNECT, and both `ChatController.sendMessage` and `ChatServiceImpl.saveMessage` verify the sender against the authenticated principal.
- **Frontend API base URLs are env-driven** everywhere via `VITE_API_BASE_URL` — no more scattered hardcoded `localhost` values in the API layer.
- **`.env.example` exists** and is secret-free; `.env` is gitignored and actually untracked.
- Automated tests now cover ownership denial, price-recalculation-from-DB, and webhook-duplicate handling (`OrderServiceImplTest`, `ConcurrentPurchaseIntegrationTest`).

The old `WebSocketSecurityConfig.java` (fully commented-out, dead since it was written) has been deleted from the working tree; it did nothing at runtime, so its removal is not a regression — real WebSocket authorization now lives in the interceptor described above.

## 3. Open issues, by priority

### Priority 1 — Repo and secret hygiene

**`dist/` is committed despite being gitignored.**
114 files under `imaji_coffee_ui/imaji_coffee_ui/dist/` are tracked by git (confirmed via `git ls-files`), even though `.gitignore` lists `dist` on line 14. The ignore rule was added after the folder was already committed, so it keeps being silently re-tracked/stale in history.
- *Why it matters*: every frontend build now diffs a large committed build artifact, bloats the repo, and can go stale relative to source, misleading anyone who inspects it.
- *Solution*: `git rm -r --cached imaji_coffee_ui/imaji_coffee_ui/dist` and commit the removal; verify the deploy pipeline (Vercel) builds `dist` itself rather than depending on the committed copy.

**A real-looking JWT secret is hardcoded in a tracked file.**
`imaji_coffee_be/docker-compose.yml:43` sets `JWT_SECRET: "0123456789abcdef0123456789abcdef"` directly in a tracked compose file (Stripe/Google values alongside it are clearly placeholders like `"change_me_stripe_secret"`, but the JWT value is a usable 32-character secret).
- *Why it matters*: anyone who clones the repo and runs `docker-compose up` gets a real, working (if weak) JWT signing key baked into source control — a low-effort target if this compose file is ever reused past local dev.
- *Solution*: replace it with an obvious placeholder (`change_me_jwt_secret`) consistent with the other entries, and require `JWT_SECRET` to be supplied via `.env`/environment at compose time with no committed default.

**Auth token reads from `localStorage` are dead code.**
`checkout_method/paypal_checkout.tsx:49` and `pages/chat/chat.tsx:1388` read a token from `localStorage`, but nothing in the app ever writes one there (auth is cookie-based via `credentials: "include"`). These reads always resolve to `null`.
- *Why it matters*: it's a leftover from a prior auth design; it doesn't break anything today, but it's misleading to future readers who may assume it's live and build on it, or waste time debugging why "the token" is always null.
- *Solution*: delete both reads and whatever branches depend on them.

### Priority 2 — Backend data layer

**No schema migration tool.**
Schema changes rely on manually-ordered raw SQL files (`init.sql`, `script.sql`, `src/main/resources/sql/*/schema.sql`, `insert.sql`, `update.sql`, `promo.sql`) with no Flyway or Liquibase.
- *Why it matters*: there's no repeatable, versioned way to evolve the schema across environments, no record of what's been applied where, and no safe rollback path — a real risk once there's more than one developer or a production deploy history.
- *Solution*: introduce Flyway (simplest fit for a Spring Boot + MySQL stack), convert the existing SQL files into versioned migrations (`V1__init.sql`, `V2__...`), and let it manage schema state going forward.

**Several list endpoints are still unbounded.**
Pagination was added for chat messages (`ChatRestController.getMessagesPage`) and product search (`ProductController.search`), but `GET /api/v1/products` with no query params (`ProductController.java:47`), `getProductBySize`, `getRelatedProducts`, and `OrderServiceImpl.getAccountOrders` (lines 198-205) all still return full unbounded `List`s.
- *Why it matters*: fine at current data volume, but these will degrade linearly as the product catalog and per-customer order history grow, with no caps in place today.
- *Solution*: switch these to `Pageable`-based endpoints for consistency with the already-paginated ones; low effort since the pattern already exists in the codebase to copy from.

### Priority 2 — Frontend consistency

**API modules are inconsistent about sending credentials.**
Most RTK Query API slices share `apiSlice`/`apiCartSlice`, which set `credentials: "include"`. But `productsApi.ts`, `paymentApi.ts`, `shipMethodsApi.ts`, `spacesApi.ts`, `newsApi.ts`, `promosApi.ts`, and `eventsApi.ts` each instantiate their own `createApi`/`fetchBaseQuery` without `credentials: "include"`.
- *Why it matters*: any of these modules that later needs an authenticated call will silently fail to send the auth cookie, producing confusing 401s that look unrelated to the actual cause. It also means there's no single place to change auth/error-handling behavior for all API calls.
- *Solution*: consolidate all modules onto one shared base query (or a small factory that wraps `fetchBaseQuery` with the standard `credentials`/base URL/error handling), rather than each file re-declaring `createApi`.

**No frontend automated tests.**
No test runner (`vitest`/`jest`/`playwright`/`cypress`) is configured, and no `*.test.*`/`*.spec.*` files exist anywhere in the project.
- *Why it matters*: checkout, cart, and auth flows have no regression safety net on the frontend; the backend has coverage for the equivalent logic, but nothing catches a UI regression before a user does.
- *Solution*: add Vitest + React Testing Library for component/unit coverage, and at minimum a Playwright smoke test for login → add to cart → checkout, since that's the highest-value path to protect.

**Loading/error/empty states are ad hoc per page.**
Only 6 of 16 page files reference `isLoading` at all, and each handles it with its own inline `Spinner`/error branch; there's no shared component.
- *Why it matters*: inconsistent UX (some pages show nothing while loading, others show a spinner, error handling varies) and duplicated logic that has to be fixed N times instead of once.
- *Solution*: extract shared `LoadingState`/`ErrorState`/`EmptyState` components and adopt them incrementally as pages are touched — not worth a dedicated sweep on its own.

**Minor: unused dependency.**
`@types/react-slick` is listed in `package.json` with zero usage of `react-slick` anywhere in the source.
- *Solution*: remove it; trivial cleanup, bundle it with any other dependency touch-up rather than doing it standalone.

### Priority 3 — Chat system enhancements (still open, unchanged from prior review)

The core chat authorization and message-ownership checks are now solid (see Section 2). What remains is product/operational maturity, not security:

- Rate limiting / abuse protection on message sending.
- Reliable unread counts and read-state persistence.
- Reconnect/offline delivery handling beyond the current interceptor-level auth.
- An admin dashboard: queue view, explicit assignment/acceptance, reassignment, internal notes, response-time metrics.
- Typing indicators, online/offline presence, browser notifications.
- A decision on the AI support widget: keep it as a static FAQ helper, or build a real backend-connected assistant (if the latter, it needs its own auth, logging, rate limits, and prompt-injection defenses before shipping).

### Priority 3 — Product ideas (unchanged, deferred until the above lands)

Reviews/ratings, favorites/wishlists, loyalty points, subscriptions, personalized recommendations, gift cards, bundles, local pickup slots — all reasonable directions once the operational items above are addressed, not before.

## 4. Testing gaps summary

| Area | Backend | Frontend |
|---|---|---|
| Authorization (ownership, admin-only) | Covered (`OrderServiceImplTest`) | None |
| Checkout price integrity | Covered | None |
| Webhook idempotency | Covered | N/A |
| Concurrent stock/purchase | Covered (`ConcurrentPurchaseIntegrationTest`) | N/A |
| Any UI flow (login, cart, checkout) | N/A | None — no test runner configured |

The backend has meaningfully better coverage of the highest-risk logic than the frontend does. The frontend gap is now the bigger relative risk.

## 5. Recommended next milestone

**Repo hygiene and pagination pass, then frontend test foundation.**

The dangerous class of bugs (auth bypass, price tampering, webhook replay) is already handled and tested. The next highest-leverage work is cheap and mechanical: stop tracking `dist/`, fix the hardcoded compose secret, remove the dead `localStorage` reads, and finish the pagination sweep — all Priority 1/2 items above, each small and independent. After that, invest in a minimal frontend test setup (Vitest + one Playwright checkout smoke test) before building new customer-facing features like the admin dashboard or loyalty program, so future changes to checkout/cart/auth have a regression net on both ends of the stack.
