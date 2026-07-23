CREATE TABLE cart (
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    promo_id BIGINT NULL,
    ship_method_id BIGINT NULL,
    created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by     VARCHAR(20)                           NOT NULL,
    updated_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by     VARCHAR(20) DEFAULT NULL,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_cart_promo FOREIGN KEY (promo_id) REFERENCES promos(id),
    CONSTRAINT fk_cart_ship FOREIGN KEY (ship_method_id) REFERENCES ship(method_id)
);

CREATE TABLE cart_item
(
    cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    cart_id BIGINT NOT NULL,
    quantity       INT                                   NOT NULL CHECK (quantity > 0),
    created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by     VARCHAR(20)                           NOT NULL,
    updated_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by     VARCHAR(20) DEFAULT NULL,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_cart_item_cart_id FOREIGN KEY (cart_id) REFERENCES cart (cart_id)
);
