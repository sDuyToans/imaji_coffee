CREATE TABLE user
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(100)                          NOT NULL UNIQUE,
    password   VARCHAR(255)                          NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(20)                           NOT NULL,
    updated_at TIMESTAMP   DEFAULT NULL,
    updated_by VARCHAR(20) DEFAULT NULL
);

ALTER TABLE user RENAME COLUMN id TO user_id;
ALTER table  user add column user_name varchar(255) not null unique;
ALTER TABLE user add column guest boolean default false;
