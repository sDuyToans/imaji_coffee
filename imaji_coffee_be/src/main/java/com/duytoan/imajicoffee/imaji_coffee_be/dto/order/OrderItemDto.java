package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import jakarta.validation.constraints.NotNull;

public record OrderItemDto(@NotNull(message = "Product Id is required") Long productId,
                           @NotNull(message = "Quantity is required") Integer quantity) {
}
