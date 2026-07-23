package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

import java.math.BigDecimal;

public record AdminLowStockDto(
        Long productId,
        String productName,
        String category,
        Integer currentStock,
        BigDecimal unitPrice
) {
}
