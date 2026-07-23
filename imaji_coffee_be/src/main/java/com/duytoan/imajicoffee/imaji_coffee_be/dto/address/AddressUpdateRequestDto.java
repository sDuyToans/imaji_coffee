package com.duytoan.imajicoffee.imaji_coffee_be.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequestDto(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        String country,

        @Size(max = 100, message = "Province must be 100 characters or fewer")
        String province,

        @NotBlank(message = "City is required")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        String city,

        @NotBlank(message = "Street is required")
        @Size(min = 5, max = 255, message = "Street must be between 5 and 255 characters")
        String street,

        @Pattern(regexp = "^[A-Za-z0-9\\- ]{3,10}$", message = "Postal code must be valid")
        String postalCode,

        @Size(max = 50, message = "Apartment must be 50 characters or fewer")
        String apartment,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        String phoneNumber,

        Boolean isDefault
) {
}
