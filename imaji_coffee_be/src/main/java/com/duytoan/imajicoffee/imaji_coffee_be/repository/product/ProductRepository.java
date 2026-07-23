package com.duytoan.imajicoffee.imaji_coffee_be.repository.product;


import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product repository
 * @author duytoan
 * @since 10/2025
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Filter product by category and pageable
     * @return products page
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Filter product by category, name, and price
     */
    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(
            @Param("category") String category,
            @Param("search") String search,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Find product by product id with pessimistic lock for update
     * Why: acquires DB write lock inside the same transaction to prevent concurrent decrements.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByProductIdForUpdate(@Param("productId") Long productId);


    /**
     * Filter related products by category and exclude current product by using product id
     */
    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:excludeId IS NULL OR p.productId <> :excludeId)")
    Page<Product> getRelatedProducts(@Param("category") String category, @Param("excludeId") Long excludeId, Pageable pageable);

    List<Product> findByIsAvailableAtWebTrueAndQuantityLessThanEqual(Integer quantity);
}
