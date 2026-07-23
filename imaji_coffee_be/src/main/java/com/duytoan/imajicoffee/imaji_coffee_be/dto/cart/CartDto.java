package com.duytoan.imajicoffee.imaji_coffee_be.dto.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.PromoDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoValidationDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ShipMethodDto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long cartId,
        Long userId,
         List<CartItemResponseDto> cartItems,
         ShipMethodDto shipMethod,
         PromoDto promo,
         PromoValidationDto promoValidation,
         BigDecimal subtotal,
         BigDecimal tax,
         BigDecimal shipping,
         BigDecimal discount,
         BigDecimal total
) {}
