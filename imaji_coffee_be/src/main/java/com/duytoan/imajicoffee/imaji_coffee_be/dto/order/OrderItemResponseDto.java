package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long productId,
        String productName,
        String productImg,
        String productCategory,
        BigDecimal price,
        Integer quantity
) {
}
