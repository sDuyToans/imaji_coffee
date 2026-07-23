CREATE TABLE addresses
(
    address_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT      DEFAULT NULL,                       -- NULL for guests, will link later
    email        VARCHAR(100)                          NOT NULL, -- always store email
    full_name    VARCHAR(100)                          NOT NULL,
    street       VARCHAR(255)                          NOT NULL,
    city         VARCHAR(100)                          NOT NULL,
    province     VARCHAR(100),
    postal_code  VARCHAR(20),
    country      VARCHAR(2)                            NOT NULL, -- ISO 2-char country code
    phone_number VARCHAR(20),
    is_default   BOOLEAN     DEFAULT FALSE,
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by   VARCHAR(20)                           NOT NULL,
    updated_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by   VARCHAR(20) DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE SET NULL,
    INDEX idx_addresses_email (email),
    INDEX idx_address_user_id (user_id)
);
