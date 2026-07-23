package com.duytoan.imajicoffee.imaji_coffee_be.dto.promo;

import java.math.BigDecimal;
import java.time.Instant;

public record PromoValidationDto(
        boolean accepted,
        String message,
        Long promoId,
        String code,
        String discountType,
        BigDecimal discountAmount,
        BigDecimal subtotal,
        BigDecimal shipping,
        BigDecimal tax,
        BigDecimal total,
        Instant expiresAt,
        String eligibilityHint
) {
}
