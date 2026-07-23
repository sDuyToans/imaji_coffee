package com.duytoan.imajicoffee.imaji_coffee_be.dto.auth;

public record SignUpRequestDto(
        String email,
        String username,
        String password
) {
}
