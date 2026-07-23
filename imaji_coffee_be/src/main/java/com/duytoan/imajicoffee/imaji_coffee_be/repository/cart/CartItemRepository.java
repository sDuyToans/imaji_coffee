package com.duytoan.imajicoffee.imaji_coffee_be.repository.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Cart item repository
 * @author duytoan
 * @since 10/2025
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Find cart item by cartItemId and userId
     * @param cartItemId
     * @param userId
     * @return optional cart item object
     */
    Optional<CartItem> findByIdAndCart_User_UserId(Long cartItemId, Long userId);
}
