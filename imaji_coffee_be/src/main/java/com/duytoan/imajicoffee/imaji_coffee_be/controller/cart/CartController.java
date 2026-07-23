package com.duytoan.imajicoffee.imaji_coffee_be.controller.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.ApplyPromoCodeRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.cart.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Cart Controller -> Mapping related to Cart Functions
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final ICartService cartService;

    /**
     * Get userId
     * @param authentication -> auth class obj
     * @return userId from Authentication Object
     */
    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

    /**
     * Get Cart by userId
     * @param authentication -> auth class obj
     * @return Cart Object with current userId
     */
    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.getCart(userId));
    }

    /**
     * Update Shipping Method in current Cart Object
     * @param authentication -> auth class obj
     * @param shipId -> long ship id
     * @return CartDto Object contains the ShipMethodDto Object updated
     */
    @PatchMapping("/shipping/{shipId}")
    public ResponseEntity<CartDto> updateShipping(Authentication authentication, @PathVariable Long shipId) {
        Long userId = getUserId(authentication);
        CartDto cartDto = cartService.updateShipping(userId, shipId);
        return ResponseEntity.ok().body(cartDto);
    }

    /**
     * Update The Promo in current Cart Object
     * @param authentication -> auth class obj
     * @param promoId -> long promo id
     * @return CartDto Object contains the PromoDto Object updated
     */
    @PatchMapping("/promo/{promoId}")
    public ResponseEntity<CartDto> updatePromo(Authentication authentication, @PathVariable(required = false) Long promoId) {
        Long userId = getUserId(authentication);
        CartDto cartDto = cartService.updatePromo(userId, promoId);
        return ResponseEntity.ok().body(cartDto);
    }

    @PatchMapping("/promo/apply")
    public ResponseEntity<CartDto> applyPromoCode(
            Authentication authentication,
            @Valid @RequestBody ApplyPromoCodeRequestDto request
    ) {
        Long userId = getUserId(authentication);
        CartDto cartDto = cartService.applyPromoCode(userId, request.code());
        return ResponseEntity.ok().body(cartDto);
    }

    /**
     * Delete Cart
     * @param authentication -> auth class obj
     * @return Void -> could change to message OK later
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear PromoDto from current Cart Object
     * @param authentication -> auth class obj
     * @return Cart Object with empty PromoDto Object
     */
    @DeleteMapping("/promo")
    public ResponseEntity<CartDto> clearPromo(Authentication authentication) {
        Long userId = getUserId(authentication);
        CartDto cartDto = cartService.updatePromo(userId, null); // null means clear
        return ResponseEntity.ok().body(cartDto);
    }

    /**
     * Clear ShipMethodDto from current Cart Object
     * @param authentication -> auth class obj
     * @return Cart Object with empty ShipMethodDto
     */
    @PostMapping("/clearShip")
    public ResponseEntity<CartDto> clearShipping(Authentication authentication) {
        Long userId = getUserId(authentication);
        CartDto cartDto = cartService.clearShipping(userId);
        return ResponseEntity.ok().body(cartDto);
    }
}
