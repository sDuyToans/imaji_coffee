package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import java.math.BigDecimal;

public record OrderResponseDto(
        Long orderId,
        String status,
        String paymentStatus,
        String clientSecret,
        BigDecimal subtotalAmount,
        BigDecimal taxAmount,
        BigDecimal shippingAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String currency
) {
}
