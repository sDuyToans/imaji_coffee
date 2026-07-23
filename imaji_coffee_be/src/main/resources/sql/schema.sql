CREATE TABLE products
(
    product_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255)                                      NOT NULL,
    description         TEXT,
    price               DECIMAL(10, 2)                                    NOT NULL,
    old_price           DECIMAL(10, 2),
    is_available_at_web BOOLEAN                                                    DEFAULT TRUE,
    quantity            INT                                                        DEFAULT 30,
    category            ENUM ('coffee_baverage', 'food_snack', 'at_home') NOT NULL DEFAULT 'coffee_baverage',
    created_at          TIMESTAMP                                                  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by          VARCHAR(20)                                       NOT NULL,
    updated_at          TIMESTAMP                                                  DEFAULT NULL,
    updated_by          VARCHAR(20)                                                DEFAULT NULL
);


CREATE TABLE product_images
(
    product_image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id       BIGINT                                NOT NULL,
    image_url        VARCHAR(500)                          NOT NULL,
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(20)                           NOT NULL,
    updated_at       TIMESTAMP   DEFAULT NULL,
    updated_by       VARCHAR(20) DEFAULT NULL,
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE
);


CREATE TABLE news
(
    new_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255)                          NOT NULL,
    description TEXT,
    image       VARCHAR(500)                          NOT NULL,
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  VARCHAR(20)                           NOT NULL,
    updated_at  TIMESTAMP   DEFAULT NULL,
    updated_by  VARCHAR(20) DEFAULT NULL
);

CREATE TABLE event
(
    event_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255)                          NOT NULL,
    image      VARCHAR(500)                          NOT NULL,
    start_date TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    duration   TIME                                  NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20)                           NOT NULL,
    updated_at TIMESTAMP   DEFAULT NULL,
    updated_by VARCHAR(20) DEFAULT NULL
);

CREATE TABLE space
(
    space_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255)                          NOT NULL,
    type       VARCHAR(50)                           NOT NULL,
    image      VARCHAR(500)                          NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20)                           NOT NULL,
    updated_at TIMESTAMP   DEFAULT NULL,
    updated_by VARCHAR(20) DEFAULT NULL
);

CREATE TABLE promos
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(50)                           NOT NULL,
    title          VARCHAR(255)                          NOT NULL,
    description    TEXT,
    discount_type  ENUM ('percentage', 'fixed', 'free_shipping'),
    discount_value DECIMAL(10, 2),
    start_at       TIMESTAMP,
    end_at         TIMESTAMP,
    is_active      BOOLEAN     DEFAULT TRUE,
    status         VARCHAR(20) DEFAULT 'ACTIVE'          NOT NULL,
    minimum_order_amount DECIMAL(10,2),
    max_total_uses INT,
    max_uses_per_user INT,
    usage_count INT DEFAULT 0                            NOT NULL,
    eligible_category VARCHAR(50),
    restricted_user_id BIGINT,
    stackable BOOLEAN DEFAULT FALSE                      NOT NULL,
    created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by     VARCHAR(20)                           NOT NULL,
    updated_at     TIMESTAMP   DEFAULT NULL,
    updated_by     VARCHAR(20) DEFAULT NULL
);

CREATE TABLE promo_products
(
    promo_id   BIGINT                                NOT NULL,
    product_id BIGINT                                NOT NULL,
    PRIMARY KEY (promo_id, product_id),
    FOREIGN KEY (promo_id) REFERENCES promos (id),
    FOREIGN KEY (product_id) REFERENCES products (product_id),
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20)                           NOT NULL,
    updated_at TIMESTAMP   DEFAULT NULL,
    updated_by VARCHAR(20) DEFAULT NULL
);

CREATE TABLE promo_usage
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
