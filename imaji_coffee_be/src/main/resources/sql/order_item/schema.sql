CREATE TABLE order_items
(
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT                                NOT NULL,
    product_id    BIGINT                                NOT NULL,
    product_name  VARCHAR(255)                          NOT NULL,
    price         DECIMAL(10, 2)                        NOT NULL,
    quantity      INT                                   NOT NULL,
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by    VARCHAR(20)                           NOT NULL,
    updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by    VARCHAR(20) DEFAULT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE RESTRICT
);

ALTER TABLE order_items ADD COLUMN product_img VARCHAR(512) NOT NULL;
ALTER TABLE order_items ADD COLUMN product_category ENUM ('coffee_baverage', 'food_snack', 'at_home') NOT NULL;
CREATE INDEX idx_order_items_order_id ON order_items(order_item_id);

