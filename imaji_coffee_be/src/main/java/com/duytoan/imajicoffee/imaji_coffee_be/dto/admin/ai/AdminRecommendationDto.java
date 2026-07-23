package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

public record AdminRecommendationDto(
        String title,
        String recommendation,
        String confidence
) {
}
