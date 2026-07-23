# Imaji Coffee — Project Review and Roadmap

This document records the current project assessment and the recommended implementation order. It is intended to be read before starting the next development phase.

No application code is changed by this document.

## 1. Current project overview

Imaji Coffee is a full-stack coffee e-commerce application with:

- React, TypeScript, Vite, HeroUI, Tailwind CSS, and Redux Toolkit frontend
- Spring Boot, Java 17, Spring Data JPA, and Spring Security backend
- MySQL database
- Product catalog, search, categories, promotions, cart, shipping, and checkout
- Stripe and PayPal-related payment flows
- JWT authentication, role-based access, and Google OAuth
- Customer accounts, addresses, order history, news, events, spaces, and FAQs
- Customer-admin real-time chat using WebSockets/STOMP
- Caffeine caching, email support, Actuator, Docker, and integration tests

The project has a solid feature foundation. Chat and FAQ functionality are currently among the most active areas. Both the backend and frontend contain ongoing uncommitted work, so behavior should be verified before starting large new features.

## 2. Highest-priority improvements

### Priority 0 — Security and authorization

This work should happen before expanding the feature set.

- Review every endpoint and require authentication by default.
- Ensure customers can only read and modify their own carts, orders, addresses, and conversations.
- Ensure only authorized admins can view admin queues, reassign conversations, manage content, and change order states.
- Protect chat conversation creation, conversation lists, message history, reassignment, and closing operations.
- Prevent users from manually setting sensitive order statuses such as `PAID`, `REFUNDED`, or `CANCELLED`.
- Add authorization tests for customer, admin, guest, and unauthenticated requests.
- Use the configured CORS environment value instead of a hardcoded localhost origin.
- Remove default passwords, JWT secrets, payment secrets, and OAuth placeholders from deployment configuration.
- Keep database runtime files, private keys, certificates, and local database data outside source control.

### Priority 0 — Checkout and payment correctness

The server must be the source of truth for order pricing.

- Accept product IDs and quantities from the client, not a trusted final total.
- Recalculate product prices from the database.
- Validate product availability and stock on the backend.
- Recalculate promotions, tax, shipping, discounts, and the final total server-side.
- Create Stripe payment intents using the server-calculated amount.
- Treat the Stripe webhook as the authoritative payment confirmation.
- Make webhook processing idempotent so repeated events do not corrupt an order.
- Define behavior for payment failure, cancellation, expiration, retry, refund, and partial fulfillment.
- Decide whether stock is reserved at order creation or deducted only after successful payment.
- Add tests for price tampering, duplicate webhooks, insufficient stock, and payment retries.

### Priority 1 — Configuration and project hygiene

- Standardize frontend API configuration across all API modules.
- Choose one authentication strategy, preferably secure HttpOnly cookies or a clearly documented alternative.
- Remove hardcoded localhost URLs and values such as `userId: 1`.
- Align frontend documentation with the actual backend port and API base path.
- Avoid committing generated `dist` output unless deployment specifically requires it.
- Review dependency versions and remove dependencies that are not needed in production.
- Separate local, test, staging, and production configuration.
- Add a safe example environment file such as `.env.example` without real secrets.

## 3. Chat system roadmap

The chat system is a valuable differentiating feature, but needs operational hardening.

### Reliability and security

- Enforce conversation ownership and admin permissions in the service layer.
- Add message length validation, rate limiting, and abuse protection.
- Add pagination for conversation history.
- Store read state and unread counts reliably on the backend.
- Handle WebSocket reconnects, duplicate messages, and offline delivery.
- Add server-side authorization for subscriptions and destinations.
- Replace most three-second polling with event-driven WebSocket updates.
- Add conversation audit history for assignment, reassignment, closure, and status changes.

### Admin workflow

- Create an admin dashboard showing waiting, open, and closed conversations.
- Add explicit conversation acceptance and assignment states.
- Show queue position and estimated waiting time to customers.
- Add internal admin-only notes and conversation tags.
- Track first response time, resolution time, workload per admin, and customer satisfaction.
- Add escalation and handoff between admins.

### Customer experience

- Show assignment and status notifications.
- Add typing indicators and online/offline status.
- Add browser notifications and optional sound alerts.
- Support message search and conversation history.
- Add file/image attachments only after storage, validation, and security requirements are defined.

### AI support decision

The current AI support UI behaves like a local FAQ simulator. Decide between:

1. Keeping it as a static FAQ helper, or
2. Building a backend-powered assistant connected to approved FAQ, product, shipping, and order-status data.

If a real assistant is built, add authentication, privacy controls, logging, rate limits, prompt-injection defenses, and a clear escalation path to a human admin.

## 4. Frontend improvements

- Create one shared API client with consistent base URL, credentials, token handling, error handling, and refresh behavior.
- Add typed request and response models to every API module.
- Centralize loading, empty, error, and retry states.
- Remove commented-out legacy cart state once the backend cart is confirmed stable.
- Add reusable form validation and accessible error messages.
- Verify mobile checkout, navigation, chat, and payment behavior.
- Improve accessibility: keyboard navigation, focus management, labels, color contrast, and screen-reader feedback.
- Add analytics for product views, cart additions, checkout drop-off, successful purchases, and support requests.
- Add frontend unit tests and browser-level checkout tests.

## 5. Backend and data improvements

- Add database indexes for conversations, messages, orders, and frequently searched products.
- Verify foreign keys and deletion behavior across all related tables.
- Use database migrations such as Flyway or Liquibase instead of relying on manually ordered SQL files.
- Add consistent API error responses with stable error codes.
- Add request correlation IDs and structured logging.
- Add metrics for checkout failures, payment events, stock conflicts, chat latency, and API errors.
- Add health checks for the database, payment integration, email service, and WebSocket layer.
- Add pagination and sorting to all potentially large list endpoints.
- Review transaction boundaries around order creation, stock changes, payment creation, and email sending.
- Make email delivery asynchronous so checkout is not blocked by an email provider.

## 6. Testing plan

### Security tests

- Unauthenticated access to protected endpoints
- Customer access to another customer's order
- Customer access to another customer's conversation
- Non-admin access to admin operations
- Unauthorized WebSocket connection and subscription
- CORS and cookie behavior in each environment

### Commerce tests

- Backend price recalculation
- Promotion validity and expiration
- Tax and shipping calculations
- Stock limits and concurrent purchases
- Duplicate checkout requests
- Stripe webhook signature validation
- Duplicate and out-of-order webhook events
- Payment failure, cancellation, refund, and retry flows

### Chat tests

- Conversation creation and assignment
- Admin reassignment
- Closed conversation behavior
- Message authorization
- Message ordering and duplicate prevention
- Reconnect and offline recovery
- Pagination and unread counts

### Frontend tests

- Login and registration
- Product filtering and pagination
- Cart and promotion behavior
- Checkout validation
- Payment success and failure states
- Order history and order access
- Responsive navigation and support widget

## 7. Recommended implementation phases

### Phase 1 — Secure the foundation

1. Inventory all endpoints and classify them as public, customer, admin, or system-only.
2. Fix authorization and ownership checks.
3. Protect payment, webhook, order, and chat operations.
4. Remove hardcoded secrets and local-only configuration.
5. Add authorization regression tests.

### Phase 2 — Make checkout authoritative

1. Recalculate orders on the backend.
2. Validate stock, promotions, tax, and shipping.
3. Make Stripe webhooks authoritative and idempotent.
4. Define order and payment state transitions.
5. Add end-to-end checkout tests.

### Phase 3 — Stabilize operations

1. Add database migrations and indexes.
2. Improve logging, metrics, health checks, and error responses.
3. Make email processing asynchronous.
4. Add pagination and performance checks.
5. Create a repeatable staging deployment.

### Phase 4 — Complete the admin experience

1. Build the admin dashboard.
2. Add product, inventory, promotion, FAQ, news, and event management.
3. Add chat queue management and support metrics.
4. Add order fulfillment and refund workflows.

### Phase 5 — Grow the customer product

1. Product reviews and ratings
2. Favorites and wishlists
3. Better order tracking and notifications
4. Loyalty points and referral rewards
5. Coffee subscriptions and recurring orders
6. Personalized product recommendations
7. Coffee education, tastings, and community events

## 8. Product ideas

The most promising direction is to make Imaji Coffee more than a basic online store by combining commerce with community and support:

- Coffee subscriptions with delivery preferences
- Loyalty points, rewards, and birthday offers
- Personalized recommendations based on taste preferences and purchase history
- Brew guides and educational coffee content
- Event registration and in-store tasting reservations
- Gift cards and gift subscriptions
- Bundles such as starter kits and seasonal collections
- Local pickup and delivery time slots
- Customer reviews with preparation and flavor tags
- Human support connected directly to the customer's order and account context

## 9. Definition of a reliable first release

Before calling the project production-ready, verify that:

- No customer can access another customer's data.
- No client can manipulate the final order price or payment status.
- Stripe webhook processing is signed, idempotent, and tested.
- Stock cannot become negative during concurrent purchases.
- Secrets and local database files are not committed or deployed accidentally.
- Checkout works for success, failure, cancellation, retry, and timeout cases.
- Admin actions are separated from customer actions.
- Critical API and checkout flows have automated tests.
- Production configuration is separate from local development configuration.
- Monitoring can detect payment failures, stock conflicts, API errors, and chat outages.

## 10. Suggested immediate milestone

**Secure and validate checkout end-to-end.**

This milestone should be completed before adding major new customer-facing features. It establishes a trustworthy core for inventory, orders, payments, and customer data, after which the admin dashboard and enhanced chat system can be developed safely.
