package com.duytoan.imajicoffee.imaji_coffee_be.repository.order;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Order repository
 * @author duytoan
 * @since 10/2025
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Find order by payment intent id
     * @param paymentIntentId
     * @return order
     */
    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    /**
     * Find orders by user id
     * @param userId
     * @return order list
     */
    List<Order> findByUserId(Long userId);

    Optional<Order> findByUserIdAndCheckoutRequestId(Long userId, String checkoutRequestId);

    Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByOrderIdForUpdate(@Param("orderId") Long orderId);

    List<Order> findByCreatedAtBetween(Instant from, Instant to);

    long countByCreatedAtBetween(Instant from, Instant to);

    long countByStatusAndCreatedAtBetween(OrderStatus status, Instant from, Instant to);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    BigDecimal sumTotalAmountByCreatedAtBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status IN :statuses AND o.createdAt BETWEEN :from AND :to")
    BigDecimal sumTotalAmountByStatusesAndCreatedAtBetween(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    @Query("SELECT o.userId, COUNT(o) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to GROUP BY o.userId")
    List<Object[]> countByUserForStatusAndRange(
            @Param("status") OrderStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}
