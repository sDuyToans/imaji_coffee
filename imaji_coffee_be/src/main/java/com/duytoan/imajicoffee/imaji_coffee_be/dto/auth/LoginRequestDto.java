package com.duytoan.imajicoffee.imaji_coffee_be.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginRequestDto(
        @NotBlank(message = "Email can't be empty")
        @Email(message = "Invalid email address")
        String loginInput,

        @Size(min = 6,max = 15, message = "Password is between 6 and 15 characters")
        String password
) {
}
