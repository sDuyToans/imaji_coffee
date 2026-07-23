package com.duytoan.imajicoffee.imaji_coffee_be.repository.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PromoUsageRepository extends JpaRepository<PromoUsage, Long> {
    long countByPromo_PromoIdAndUserId(Long promoId, Long userId);

    Optional<PromoUsage> findByOrderId(Long orderId);

    @Query("""
            SELECT pu.promo.code, COUNT(pu)
            FROM PromoUsage pu
            WHERE pu.usedAt BETWEEN :from AND :to
            GROUP BY pu.promo.code
            ORDER BY COUNT(pu) DESC
            """)
    List<Object[]> countPromoUsageByCode(@Param("from") Instant from, @Param("to") Instant to);

    @Query("""
            SELECT pu.userId, COUNT(pu)
            FROM PromoUsage pu
            WHERE pu.usedAt BETWEEN :from AND :to
            GROUP BY pu.userId
            ORDER BY COUNT(pu) DESC
            """)
    List<Object[]> countPromoUsageByUser(@Param("from") Instant from, @Param("to") Instant to);
}
