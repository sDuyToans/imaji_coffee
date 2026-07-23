package com.duytoan.imajicoffee.imaji_coffee_be.dto.promo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PromoAdminResponseDto(
        Long promoId,
        String code,
        String title,
        String description,
        String discountType,
        BigDecimal discountValue,
        Instant startAt,
        Instant endAt,
        Boolean isActive,
        String status,
        BigDecimal minimumOrderAmount,
        Integer maxTotalUses,
        Integer maxUsesPerUser,
        Integer usageCount,
        String eligibleCategory,
        Long restrictedUserId,
        Boolean stackable,
        List<Long> productIds,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
