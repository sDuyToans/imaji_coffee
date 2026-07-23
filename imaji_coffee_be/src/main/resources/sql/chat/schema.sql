CREATE TABLE chat_conversation
(
    conversation_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    customer_id     BIGINT      NOT NULL,
    admin_id        BIGINT NULL,

    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- OPEN | CLOSED | PENDING
    customer_last_read_message_id BIGINT DEFAULT NULL,
    admin_last_read_message_id    BIGINT DEFAULT NULL,

    created_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by      VARCHAR(20) NOT NULL,
    updated_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      VARCHAR(20)          DEFAULT NULL,

    CONSTRAINT fk_chat_customer FOREIGN KEY (customer_id) REFERENCES user (user_id),
    CONSTRAINT fk_chat_admin FOREIGN KEY (admin_id) REFERENCES user (user_id)
);

CREATE TABLE chat_message
(
    message_id      BIGINT AUTO_INCREMENT PRIMARY KEY,

    conversation_id BIGINT                                NOT NULL,

    sender_id       BIGINT                                NOT NULL,
    sender_type     VARCHAR(20)                           NOT NULL, -- USER | ADMIN | AI

    content         TEXT                                  NOT NULL,

    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by      VARCHAR(20)                           NOT NULL,
    updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      VARCHAR(20) DEFAULT NULL,

    CONSTRAINT fk_message_conversation
        FOREIGN KEY (conversation_id) REFERENCES chat_conversation (conversation_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id) REFERENCES user (user_id)
);

ALTER TABLE chat_message
    ADD COLUMN sender_name VARCHAR(255) NULL;
