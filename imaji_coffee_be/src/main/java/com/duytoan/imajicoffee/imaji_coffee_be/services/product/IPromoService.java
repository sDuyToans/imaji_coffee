package com.duytoan.imajicoffee.imaji_coffee_be.services.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductPromosDto;

/**
 * Promo interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IPromoService {
    /**
     * Get promo object for product with productId
     * @param productId -> long
     * @return ProductPromoDto
     */
    ProductPromosDto getPromosForProduct(Long productId);
}
