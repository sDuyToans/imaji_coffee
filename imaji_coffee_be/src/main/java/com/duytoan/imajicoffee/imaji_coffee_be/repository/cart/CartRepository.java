package com.duytoan.imajicoffee.imaji_coffee_be.repository.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Cart repository
 * @author duytoan
 * @since 10/2025
 */
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Find cart by user id
     * @param userId
     * @return optional cart object
     */
    Optional<Cart> findByUser_UserId(Long userId);

    /**
     * Delete cart by user id
     * @param userId
     */
    void deleteByUser_UserId(Long userId);
}
