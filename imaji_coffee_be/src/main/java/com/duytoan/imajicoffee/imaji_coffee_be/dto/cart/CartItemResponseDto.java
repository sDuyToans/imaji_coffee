package com.duytoan.imajicoffee.imaji_coffee_be.dto.cart;

import java.math.BigDecimal;

public record CartItemResponseDto(
        Long cartItemId,
        Long productId,
        String productName,
        String productCategory,
        BigDecimal price,
        Integer quantity,
        String imageUrl
) {
}
