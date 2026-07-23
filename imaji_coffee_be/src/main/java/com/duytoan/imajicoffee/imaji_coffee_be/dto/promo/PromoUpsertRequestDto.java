package com.duytoan.imajicoffee.imaji_coffee_be.dto.promo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PromoUpsertRequestDto(
        @NotBlank(message = "Code is required")
        @Size(max = 50, message = "Code must be 50 characters or fewer")
        String code,

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be 255 characters or fewer")
        String title,

        String description,

        @NotBlank(message = "Discount type is required")
        String discountType,

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0.00", message = "Discount value must be non-negative")
        BigDecimal discountValue,

        Instant startAt,
        Instant endAt,
        Boolean isActive,
        String status,
        @DecimalMin(value = "0.00", message = "Minimum order amount must be non-negative")
        BigDecimal minimumOrderAmount,
        Integer maxTotalUses,
        Integer maxUsesPerUser,
        String eligibleCategory,
        Long restrictedUserId,
        Boolean stackable,
        List<Long> productIds
) {
}
