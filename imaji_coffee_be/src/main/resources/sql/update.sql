UPDATE promos SET end_at = DATE_ADD(NOW(), INTERVAL 1 DAY) WHERE id BETWEEN 1 AND 6;

ALTER TABLE chat_conversation
    ADD COLUMN IF NOT EXISTS customer_last_read_message_id BIGINT NULL AFTER status,
    ADD COLUMN IF NOT EXISTS admin_last_read_message_id BIGINT NULL AFTER customer_last_read_message_id;

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS external_payment_id VARCHAR(100) NULL AFTER payment_intent_id,
    ADD COLUMN IF NOT EXISTS checkout_request_id VARCHAR(100) NULL AFTER external_payment_id,
    ADD COLUMN IF NOT EXISTS stock_released BOOLEAN NOT NULL DEFAULT FALSE AFTER checkout_request_id,
    ADD COLUMN IF NOT EXISTS confirmation_email_sent BOOLEAN NOT NULL DEFAULT FALSE AFTER stock_released,
    ADD COLUMN IF NOT EXISTS confirmation_email_sent_at TIMESTAMP NULL AFTER confirmation_email_sent;

CREATE UNIQUE INDEX IF NOT EXISTS uq_orders_user_checkout_request
    ON orders (user_id, checkout_request_id);

CREATE TABLE IF NOT EXISTS payment_webhook_event
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider   VARCHAR(20)  NOT NULL,
    event_id   VARCHAR(120) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_payment_webhook_provider_event UNIQUE (provider, event_id)
);

ALTER TABLE promos
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER is_active,
    ADD COLUMN IF NOT EXISTS minimum_order_amount DECIMAL(10,2) NULL AFTER status,
    ADD COLUMN IF NOT EXISTS max_total_uses INT NULL AFTER minimum_order_amount,
    ADD COLUMN IF NOT EXISTS max_uses_per_user INT NULL AFTER max_total_uses,
    ADD COLUMN IF NOT EXISTS usage_count INT NOT NULL DEFAULT 0 AFTER max_uses_per_user,
    ADD COLUMN IF NOT EXISTS eligible_category VARCHAR(50) NULL AFTER usage_count,
    ADD COLUMN IF NOT EXISTS restricted_user_id BIGINT NULL AFTER eligible_category,
    ADD COLUMN IF NOT EXISTS stackable BOOLEAN NOT NULL DEFAULT FALSE AFTER restricted_user_id;

CREATE TABLE IF NOT EXISTS promo_usage
(
    promo_usage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    promo_id       BIGINT                                NOT NULL,
    user_id        BIGINT                                NOT NULL,
    order_id       BIGINT                                NOT NULL,
    used_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by     VARCHAR(20)                           NOT NULL,
    CONSTRAINT fk_promo_usage_promo FOREIGN KEY (promo_id) REFERENCES promos (id),
    CONSTRAINT uq_promo_usage_promo_user_order UNIQUE (promo_id, user_id, order_id)
);
