package com.duytoan.imajicoffee.imaji_coffee_be.services.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemResponseDto;

/**
 * CartItem interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface ICartItemService {

    /**
     * Add item to cart item
     * @param userId
     * @param request
     * @return CartItemRes Dto Object
     */
    CartItemResponseDto addItem(Long userId, CartItemRequestDto request);

    /**
     * Update quantity for item in cart item
     * @param userId
     * @param cartItemId
     * @param quantity
     * @return CartItemRes Dto Object
     */
    CartItemResponseDto updateItem(Long userId, Long cartItemId, int quantity);

    /**
     * Remove item from cart item
     * @param userId
     * @param cartItemId
     */
    void removeItem(Long userId, Long cartItemId);
}
