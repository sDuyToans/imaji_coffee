package com.duytoan.imajicoffee.imaji_coffee_be.repository.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Product image repository
 * @author duytoan
 * @since 10/2025
 */
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
