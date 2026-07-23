package com.duytoan.imajicoffee.imaji_coffee_be.config.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSchemaMigrationRunner implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        if (!isMySql()) {
            return;
        }

        ensureColumn(
                "orders",
                "external_payment_id",
                "ALTER TABLE orders ADD COLUMN external_payment_id VARCHAR(100) NULL AFTER payment_intent_id"
        );
        ensureColumn(
                "orders",
                "checkout_request_id",
                "ALTER TABLE orders ADD COLUMN checkout_request_id VARCHAR(100) NULL AFTER external_payment_id"
        );
        ensureColumn(
                "orders",
                "stock_released",
                "ALTER TABLE orders ADD COLUMN stock_released BOOLEAN NOT NULL DEFAULT FALSE AFTER checkout_request_id"
        );
        ensureColumn(
                "orders",
                "confirmation_email_sent",
                "ALTER TABLE orders ADD COLUMN confirmation_email_sent BOOLEAN NOT NULL DEFAULT FALSE AFTER stock_released"
        );
        ensureColumn(
                "orders",
                "confirmation_email_sent_at",
                "ALTER TABLE orders ADD COLUMN confirmation_email_sent_at TIMESTAMP NULL AFTER confirmation_email_sent"
        );
        ensureColumn(
                "orders",
                "payment_status",
                "ALTER TABLE orders ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' AFTER status"
        );
        backfillPaymentStatus();

        ensureColumn(
                "user",
                "phone",
                "ALTER TABLE user ADD COLUMN phone VARCHAR(20) NULL AFTER password"
        );
        ensureColumn(
                "user",
                "token_version",
                "ALTER TABLE user ADD COLUMN token_version INT NOT NULL DEFAULT 0 AFTER phone"
        );

        ensureTable(
                "payment_webhook_event",
                """
                CREATE TABLE payment_webhook_event
                (
                    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                    provider   VARCHAR(20)  NOT NULL,
                    event_id   VARCHAR(120) NOT NULL,
                    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT uq_payment_webhook_provider_event UNIQUE (provider, event_id)
                )
                """
        );
        ensureColumn(
                "promos",
                "status",
                "ALTER TABLE promos ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER is_active"
        );
        ensureColumn(
                "promos",
                "minimum_order_amount",
                "ALTER TABLE promos ADD COLUMN minimum_order_amount DECIMAL(10,2) NULL AFTER status"
        );
        ensureColumn(
                "promos",
                "max_total_uses",
                "ALTER TABLE promos ADD COLUMN max_total_uses INT NULL AFTER minimum_order_amount"
        );
        ensureColumn(
                "promos",
                "max_uses_per_user",
                "ALTER TABLE promos ADD COLUMN max_uses_per_user INT NULL AFTER max_total_uses"
        );
        ensureColumn(
                "promos",
                "usage_count",
                "ALTER TABLE promos ADD COLUMN usage_count INT NOT NULL DEFAULT 0 AFTER max_uses_per_user"
        );
        ensureColumn(
                "promos",
                "eligible_category",
                "ALTER TABLE promos ADD COLUMN eligible_category VARCHAR(50) NULL AFTER usage_count"
        );
        ensureColumn(
                "promos",
                "restricted_user_id",
                "ALTER TABLE promos ADD COLUMN restricted_user_id BIGINT NULL AFTER eligible_category"
        );
        ensureColumn(
                "promos",
                "stackable",
                "ALTER TABLE promos ADD COLUMN stackable BOOLEAN NOT NULL DEFAULT FALSE AFTER restricted_user_id"
        );
        ensureTable(
                "promo_usage",
                """
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
                )
                """
        );

        ensureIndex(
                "orders",
                "uq_orders_user_checkout_request",
                "CREATE UNIQUE INDEX uq_orders_user_checkout_request ON orders (user_id, checkout_request_id)"
        );
    }

    private void backfillPaymentStatus() {
        jdbcTemplate.update(
                """
                UPDATE orders
                SET payment_status = CASE
                    WHEN status = 'PAID' THEN 'PAID'
                    WHEN status = 'PAYMENT_FAILED' THEN 'FAILED'
                    ELSE 'UNPAID'
                END
                WHERE payment_status = 'UNPAID'
                """
        );
    }

    private boolean isMySql() {
        try (Connection connection = dataSource.getConnection()) {
            String dbName = connection.getMetaData().getDatabaseProductName();
            return dbName != null && dbName.toLowerCase().contains("mysql");
        } catch (Exception e) {
            throw new IllegalStateException("Cannot detect database product", e);
        }
    }

    private void ensureColumn(String tableName, String columnName, String ddl) {
        Integer exists = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                """,
                Integer.class,
                tableName,
                columnName
        );

        if (exists != null && exists > 0) {
            return;
        }

        log.info("Applying schema change: {}.{}", tableName, columnName);
        jdbcTemplate.execute(ddl);
    }

    private void ensureTable(String tableName, String ddl) {
        Integer exists = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                """,
                Integer.class,
                tableName
        );

        if (exists != null && exists > 0) {
            return;
        }

        log.info("Creating missing table: {}", tableName);
        jdbcTemplate.execute(ddl);
    }

    private void ensureIndex(String tableName, String indexName, String ddl) {
        Integer exists = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND index_name = ?
                """,
                Integer.class,
                tableName,
                indexName
        );

        if (exists != null && exists > 0) {
            return;
        }

        log.info("Creating missing index: {}", indexName);
        jdbcTemplate.execute(ddl);
    }
}
