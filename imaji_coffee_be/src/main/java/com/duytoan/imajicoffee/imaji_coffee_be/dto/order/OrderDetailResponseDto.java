package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDetailResponseDto(
        Long orderId,
        String status,
        String paymentStatus,
        BigDecimal totalAmount,
        BigDecimal taxAmount,
        BigDecimal shippingAmount,
        BigDecimal discountAmount,
        String email,
        String shippingAddress,
        String shippingMethod,
        String paymentMethod,
        Instant createdAt,
        List<OrderItemResponseDto> items
) {
}
