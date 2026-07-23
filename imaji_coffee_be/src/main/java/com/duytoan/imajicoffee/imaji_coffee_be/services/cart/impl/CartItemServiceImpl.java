package com.duytoan.imajicoffee.imaji_coffee_be.services.cart.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.CartItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.cart.Cart;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.cart.CartItemRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.cart.CartRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.cart.ICartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implemented CartItemService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements ICartItemService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ShipRepository shipRepository;

    /**
     * Add item to cart item
     * @param userId -> long userId
     * @param request -> cart item dto object
     * @return CartItemRes Dto Object
     */
    @Override
    public CartItemResponseDto addItem(Long userId, CartItemRequestDto request) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    // if there is no cart for specific user, create one
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", request.productId().toString()));

        // If cart item quantity is greater than product quantity -> throw exception
        if (product.getQuantity() < request.quantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock for product: " + product.getName());
        }

        // If product exists -> update quantity
        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getProductId().equals(product.getProductId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem nCartItem = new CartItem();
                    nCartItem.setCart(cart);
                    nCartItem.setProduct(product);
                    return nCartItem; // no save here
                });

        if (item.getId() != null) {
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            item.setQuantity(request.quantity());
        }

        cartItemRepository.save(item);

        return mapToCartItemResponseDto(item);

    }

    /**
     * Update quantity for item in cart item
     * @param userId -> long userId
     * @param cartItemId -> long cartItemId
     * @param quantity -> int quantity
     * @return CartItemRes Dto Object
     */
    @Override
    public CartItemResponseDto updateItem(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findByIdAndCart_User_UserId(cartItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "CartItemId", cartItemId.toString()));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return mapToCartItemResponseDto(cartItem);
    }

    /**
     * Remove item from cart item
     * @param userId -> long userId
     * @param cartItemId -> long cartItemId
     */
    @Override
    @Transactional
    public void removeItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId.toString()));
        CartItem cartItem = cartItemRepository.findByIdAndCart_User_UserId(cartItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "CartItemId", cartItemId.toString()));

        cartItemRepository.delete(cartItem);
        if (cart.getCartItems().isEmpty()){
            Ship freeShip = shipRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Ship", "shipMethodId", String.valueOf(1)));
            cart.setShipMethod(freeShip);
        }

        cart.getCartItems().remove(cartItem);

        cartRepository.save(cart); // persist cart to db
    }


    /**
     * Map cart item to cart item response dto
     * @param cartItem -> cartItem object
     * @return CartItemRes Dto object
     */
    private CartItemResponseDto mapToCartItemResponseDto(CartItem cartItem) {
        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getProduct().getProductId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getCategory(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity(),
                cartItem.getProduct().getMainImageUrl()
        );
    }
}
