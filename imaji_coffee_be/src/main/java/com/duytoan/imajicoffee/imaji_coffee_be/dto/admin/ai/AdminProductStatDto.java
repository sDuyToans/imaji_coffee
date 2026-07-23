package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

public record AdminProductStatDto(
        Long productId,
        String productName,
        String category,
        long quantity
) {
}
