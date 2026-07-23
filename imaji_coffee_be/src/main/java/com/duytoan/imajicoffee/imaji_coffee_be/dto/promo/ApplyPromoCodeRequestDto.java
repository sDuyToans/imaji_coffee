package com.duytoan.imajicoffee.imaji_coffee_be.dto.promo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplyPromoCodeRequestDto(
        @NotBlank(message = "Promo code is required")
        @Size(max = 50, message = "Promo code must be 50 characters or fewer")
        String code
) {
}
