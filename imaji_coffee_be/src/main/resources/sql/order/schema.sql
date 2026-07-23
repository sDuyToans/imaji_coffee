CREATE TABLE orders
(
    order_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT      DEFAULT NULL,
    email             VARCHAR(100)                          NOT NULL,
    shipping_address  JSON                                  NOT NULL,
    total_amount      DECIMAL(10, 2)                        NOT NULL,
    currency          VARCHAR(3)                            NOT NULL,
    status            ENUM (
        'PENDING',
        'PAYMENT_FAILED',
        'PAID',
        'PROCESSING',
        'SHIPPED',
        'DELIVERED',
        'CANCELLED',
        'REFUNDED'
        )                         DEFAULT 'PENDING',
    payment_intent_id VARCHAR(255),
    external_payment_id VARCHAR(100),
    checkout_request_id VARCHAR(100),
    stock_released    BOOLEAN     DEFAULT FALSE NOT NULL,
    confirmation_email_sent BOOLEAN DEFAULT FALSE NOT NULL,
    confirmation_email_sent_at TIMESTAMP NULL,
    payment_method    VARCHAR(50),
    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by        VARCHAR(20)                           NOT NULL,
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by        VARCHAR(20) DEFAULT NULL
);

ALTER TABLE orders
    ADD COLUMN tax_amount      DECIMAL(10, 2) DEFAULT 0,
    ADD COLUMN shipping_amount DECIMAL(10, 2) DEFAULT 0,
    ADD COLUMN discount_amount DECIMAL(10, 2) DEFAULT 0;

ALTER TABLE orders
ADD COLUMN shipping_method VARCHAR(100);

ALTER TABLE orders
    ADD CONSTRAINT uq_orders_user_checkout_request UNIQUE (user_id, checkout_request_id);
