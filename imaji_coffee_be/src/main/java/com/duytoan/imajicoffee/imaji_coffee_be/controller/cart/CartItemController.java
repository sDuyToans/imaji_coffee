package com.duytoan.imajicoffee.imaji_coffee_be.controller.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.UpdateQuantityCartItemRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.services.cart.ICartItemService;
import com.duytoan.imajicoffee.imaji_coffee_be.utils.GetAuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart/items")
public class CartItemController {

    private final ICartItemService cartItemService;
    private final GetAuthenticationInfo getAuthenticationInfo;

    /**
     * Add item to cart item
     * @param authentication -> Authentication object from Spring Security
     * @param cartItemRequestDto -> CartItemRequestDto object containing productId and quantity
     * @return ResponseEntity containing CartItemResponseDto object and HTTP status code
     */
    @PostMapping
    public ResponseEntity<CartItemResponseDto> addItem(Authentication authentication, @RequestBody CartItemRequestDto cartItemRequestDto) {
        Long userId = getAuthenticationInfo.getUserId();
        CartItemResponseDto cartItemResponseDto = cartItemService.addItem(userId, cartItemRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemResponseDto);
    }

    /**
     * Update quantity for item in cart item
     * @param authentication -> Authentication object from Spring Security
     * @param cartItemId -> ID of the cart item to be updated
     * @param request -> UpdateQuantityCartItemRequest object containing the new quantity
     * @return ResponseEntity containing CartItemResponseDto object and HTTP status code
     */
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponseDto> updateItem(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestBody UpdateQuantityCartItemRequest request) {
        Long userId = getAuthenticationInfo.getUserId();
        CartItemResponseDto updatedCartItemResponseDto = cartItemService.updateItem(userId, cartItemId, request.getQuantity());
        return ResponseEntity.status(HttpStatus.OK).body(updatedCartItemResponseDto);
    }

    /**
     * Remove item from cart item
     * @param authentication -> Authentication object from Spring Security
     * @param cartItemId -> ID of the cart item to be removed
     * @return ResponseEntity with HTTP status code indicating the result of the operation
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable Long cartItemId) {
        Long userId = getAuthenticationInfo.getUserId();
        cartItemService.removeItem(userId, cartItemId);

        return ResponseEntity.noContent().build();
    }
}
