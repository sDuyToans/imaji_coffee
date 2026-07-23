package com.duytoan.imajicoffee.imaji_coffee_be.services.cart.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartItemResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.PromoDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoValidationDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ShipMethodDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.CartItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.cart.Cart;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.cart.CartRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.cart.ICartService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implemented CartService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements ICartService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final CartRepository cartRepository;
    private final ShipRepository shipRepository;
    private final PromoRepository promoRepository;
    private final PromoPricingService promoPricingService;

    /**
     * Get current user cart by userId
     * @param userId -> long userId
     * @return CartDto Object
     */
    @Override
    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId).orElseThrow(
                () -> new ResourceNotFoundException("Cart", "UserId", userId.toString())
        );
        return mapToCartDto(cart);
    }

    /**
     * Update ShippingMethodDto in CartDto Object
     * @param userId -> long userId
     * @param shippingId -> long shippingId
     * @return CartDto Updated
     */
    @Override
    public CartDto updateShipping(Long userId, Long shippingId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId.toString()));

        Ship ship = shipRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping", "ShippingId", shippingId.toString()));

        cart.setShipMethod(ship);
        cartRepository.save(cart);
       return mapToCartDto(cart);
    }

    /**
     * Update PromoDto Object in CartDto Object
     * @param userId -> long userId
     * @param promoId -> long promoId
     * @return Updated CartDto Object
     */
    @Override
    public CartDto updatePromo(Long userId, Long promoId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "UserId", userId.toString()));

        if (promoId == null) {
            // Clear promo if null
            cart.setPromo(null);
        } else {
            Promo promo = promoRepository.findById(promoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Promo", "Id", promoId.toString()));
            return applyPromoCode(userId, promo.getCode());
        }

        cartRepository.save(cart);
        return mapToCartDto(cart);
    }

    @Override
    public CartDto applyPromoCode(Long userId, String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Promo code is required");
        }

        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "UserId", userId.toString()));

        Promo promo = promoRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Promo code is invalid"));

        BigDecimal subtotal = subtotal(cart);
        BigDecimal shipping = shipping(cart);
        List<OrderItem> orderItems = toOrderItems(cart);
        PromoPricingSnapshot evaluation = promoPricingService.evaluate(
                promo,
                userId,
                orderItems,
                subtotal,
                shipping,
                true
        );

        if (!evaluation.accepted()) {
            throw new IllegalArgumentException(evaluation.message());
        }

        if (cart.getPromo() != null && !cart.getPromo().getPromoId().equals(promo.getPromoId()) && !Boolean.TRUE.equals(promo.getStackable())) {
            throw new IllegalArgumentException("Only one promo code can be applied at a time");
        }

        cart.setPromo(promo);
        cartRepository.save(cart);
        return mapToCartDto(cart);
    }

    /**
     * Remove ShippingMethodDto from CartDto Object
     * @param userId -> long userId
     * @return Updated CartDto Object
     */
    @Override
    public CartDto clearShipping(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId.toString()));
        Ship freeShip = shipRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Ship", "shipMethodId", String.valueOf(1)));
        cart.setShipMethod(freeShip);
        cartRepository.save(cart);
        return mapToCartDto(cart);
    }

    /**
     * Clear Current CartDto Object
     * @param userId -> long userId
     */
    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId.toString()));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Map Cart Object To CartDto Object
     * @param cart -> cart object
     * @return CartDto Object
     */
    private CartDto mapToCartDto(Cart cart) {
        BigDecimal subtotal = subtotal(cart);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = shipping(cart);

        PromoValidationDto promoValidation = null;
        BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        if (cart.getPromo() != null) {
            PromoPricingSnapshot evaluation = promoPricingService.evaluate(
                    cart.getPromo(),
                    cart.getUser().getUserId(),
                    toOrderItems(cart),
                    subtotal,
                    shipping,
                    true
            );
            promoValidation = new PromoValidationDto(
                    evaluation.accepted(),
                    evaluation.message(),
                    cart.getPromo().getPromoId(),
                    cart.getPromo().getCode(),
                    cart.getPromo().getDiscountType(),
                    evaluation.discountAmount(),
                    subtotal,
                    evaluation.shippingAmount(),
                    tax,
                    subtotal.add(tax).add(evaluation.shippingAmount()).subtract(evaluation.discountAmount()).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP),
                    cart.getPromo().getEndAt(),
                    evaluation.eligibilityHint()
            );
            if (evaluation.accepted()) {
                discount = evaluation.discountAmount();
                shipping = evaluation.shippingAmount();
            }
        }

        BigDecimal total = subtotal.add(tax).add(shipping).subtract(discount).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return new CartDto(
                cart.getId(),
                cart.getUser().getUserId(),
                cart.getCartItems().stream().map(this::mapToCartItemDto).toList(),
                cart.getShipMethod() != null ? mapToShipMethodDto(cart.getShipMethod()) : null,
                cart.getPromo() != null ? mapToPromoDto(cart.getPromo()) : null,
                promoValidation,
                subtotal, tax, shipping, discount, total
        );
    }

    private BigDecimal subtotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal shipping(Cart cart) {
        return (cart.getShipMethod() != null ? cart.getShipMethod().getPrice() : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private List<OrderItem> toOrderItems(Cart cart) {
        return cart.getCartItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProduct().getProductId());
            orderItem.setProductCategory(item.getProduct().getCategory());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItem.setQuantity(item.getQuantity());
            return orderItem;
        }).toList();
    }

    /**
     * Map CartItem To CartItemDto Object
     * @param cartItem -> cartItem object
     * @return CartItemDto Object
     */
    private CartItemResponseDto mapToCartItemDto(CartItem cartItem) {
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

    /**
     * Map Ship To ShipMethodDto Object
     * @param ship -> ship object
     * @return ShipMethodDto Object
     */
    private ShipMethodDto mapToShipMethodDto(Ship ship) {
        ShipMethodDto shipMethodDto = new ShipMethodDto();
        BeanUtils.copyProperties(ship, shipMethodDto);
        return shipMethodDto;
    }

    /**
     * Map Promo To PromoDto Object
     * @param promo -> promo object
     * @return PromoDto Object
     */
    private PromoDto mapToPromoDto(Promo promo) {
        PromoDto promoDto = new PromoDto();
        BeanUtils.copyProperties(promo, promoDto);
        return promoDto;
    }
}
