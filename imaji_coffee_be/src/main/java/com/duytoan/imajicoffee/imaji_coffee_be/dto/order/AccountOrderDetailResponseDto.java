package com.duytoan.imajicoffee.imaji_coffee_be.dto.order;

import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class AccountOrderDetailResponseDto {
    Long orderId;
    Instant createdAt;
    OrderStatus status;
    PaymentStatus paymentStatus;
    String paymentMethod;
    Integer items;
    BigDecimal amount;
}
