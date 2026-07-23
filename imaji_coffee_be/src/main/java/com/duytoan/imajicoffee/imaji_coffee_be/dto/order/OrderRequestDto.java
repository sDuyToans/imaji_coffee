package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderRequestDto(
        @NotBlank(message = "Payment method is required")
        @Pattern(regexp = "^(card|paypal|cod)$", message = "Payment method is invalid")
        String paymentMethod,

        @NotNull(message = "Shipping method id is required")
        Long shipMethodId,

        @NotNull(message = "Address is required")
        @Valid
        AddressDto shippingAddress,

        @Size(max = 50, message = "Coupon code must be 50 characters or fewer")
        String couponCode,

        @NotBlank(message = "Idempotency key is required")
        @Size(max = 100, message = "Idempotency key must be 100 characters or fewer")
        String idempotencyKey,

        @NotNull(message = "Order items is required")
        @Size(min = 1, message = "Order items must not be empty")
        @Valid
        List<OrderItemDto> items
) {
}
