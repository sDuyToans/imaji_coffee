package com.duytoan.imajicoffee.imaji_coffee_be.services.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartDto;

/**
 * CartService interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface ICartService {

    /**
     * Get current user cart by userId
     * @param userId
     * @return CartDto Object
     */
    CartDto getCart(Long userId);

    /**
     * Update ShippingMethodDto in CartDto Object
     * @param userId
     * @param shippingId
     * @return CartDto Updated
     */
    CartDto updateShipping(Long userId, Long shippingId);

    /**
     * Update PromoDto Object in CartDto Object
     * @param userId
     * @param promoId
     * @return Updated CartDto Object
     */
    CartDto updatePromo(Long userId, Long promoId);

    CartDto applyPromoCode(Long userId, String code);

    /**
     * Remove ShippingMethodDto from CartDto Object
     * @param userId
     * @return Updated CartDto Object
     */
    CartDto clearShipping(Long userId);

    /**
     * Clear Current CartDto Object
     * @param userId
     */
    void clearCart(Long userId);
}
