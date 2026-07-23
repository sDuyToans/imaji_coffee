package com.duytoan.imajicoffee.imaji_coffee_be.repository.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Promo repository
 * @author duytoan
 * @since 10/2025
 */
public interface PromoRepository extends JpaRepository<Promo, Long> {
    /**
     * Filter available promos by product id and current date
     * @param productId
     * @return available promos
     */
    @Query("SELECT p FROM Promo p JOIN p.promoProducts pp " +
            "WHERE pp.product.productId = :productId " +
            "AND p.isActive = true " +
            "AND p.startAt <= CURRENT_TIMESTAMP  " +
            "AND p.endAt >= CURRENT_TIMESTAMP ")
    List<Promo> findAvailablePromosByProductId(@Param("productId") Long productId);


    /**
     * Filter unavailable promos by product id and current date
     * @param productId
     * @return unavailable promos
     */
    @Query("SELECT p FROM Promo p " +
            "WHERE p.isActive = true " +
            "AND p.startAt <= CURRENT_TIMESTAMP " +
            "AND p.endAt >= CURRENT_TIMESTAMP " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM PromoProduct pp " +
            "   WHERE pp.promo = p AND pp.product.productId = :productId)")
    List<Promo> findUnavailablePromosByProductId(@Param("productId") Long productId);

    /**
     * Find top 3 promos order by id asc
     * @return list of promos
     */
    List<Promo> findTop3ByOrderByPromoIdAsc();

    /**
     * Find top 3 promos order by id desc
     * @return
     */
    List<Promo> findTop3ByOrderByPromoIdDesc();

    @Query("SELECT p FROM Promo p " +
            "WHERE LOWER(p.code) = LOWER(:code) " +
            "AND p.isActive = true " +
            "AND p.startAt <= CURRENT_TIMESTAMP " +
            "AND p.endAt >= CURRENT_TIMESTAMP")
    Optional<Promo> findActivePromoByCode(@Param("code") String code);

    Optional<Promo> findByCodeIgnoreCase(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Promo p WHERE LOWER(p.code) = LOWER(:code)")
    Optional<Promo> findByCodeForUpdate(@Param("code") String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Promo p WHERE p.promoId = :promoId")
    Optional<Promo> findByPromoIdForUpdate(@Param("promoId") Long promoId);
}
