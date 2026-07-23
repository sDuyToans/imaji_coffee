package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDto(@NotNull(message = "Status is required to update") OrderStatus status) {
}
