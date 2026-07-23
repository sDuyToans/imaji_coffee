package com.duytoan.imajicoffee.imaji_coffee_be.dto.address;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Setter;


public record AddressDto(

        Long addressId,

        @Setter
        Long userId,

        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotNull(message = "Country is required")
        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        String country,

        @Size(min = 2, max = 100, message = "Province must be between 2 and 100 characters")
        String province,
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        String city,
        @Size(min = 5, max = 255, message = "Street must be between 5 and 255 characters")
        String street,

        @Pattern(regexp = "^[A-Za-z0-9 ]{3,10}$", message = "Postal code must be valid")
        String postalCode,

        String apartment,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        String phoneNumber,

        Boolean isDefault
) {}
