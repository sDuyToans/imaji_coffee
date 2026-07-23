CREATE TABLE ship (
    method_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    method_name VARCHAR(100) NOT NULL,
    code VARCHAR(5) NOT NULL UNIQUE ,
    expected_arrival VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by     VARCHAR(20)                           NOT NULL,
    updated_at     TIMESTAMP   DEFAULT NULL,
    updated_by     VARCHAR(20) DEFAULT NULL
);

