package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

public record AdminAlertDto(
        String severity,
        String title,
        String description
) {
}
