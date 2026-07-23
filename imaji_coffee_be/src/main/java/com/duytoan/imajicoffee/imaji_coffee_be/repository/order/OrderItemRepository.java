package com.duytoan.imajicoffee.imaji_coffee_be.repository.order;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * Order item repository
 * @author duytoan
 * @since 10/2025
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
            SELECT oi.productId, oi.productName, oi.productCategory, SUM(oi.quantity)
            FROM OrderItem oi
            WHERE oi.order.status IN ('PAID','PROCESSING','SHIPPED','DELIVERED')
              AND oi.order.createdAt BETWEEN :from AND :to
            GROUP BY oi.productId, oi.productName, oi.productCategory
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Object[]> findTopSellingProducts(
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}
