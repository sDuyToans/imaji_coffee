package com.duytoan.imajicoffee.imaji_coffee_be.dto.payment;

public record PaymentIntentRequestDto(Long orderId, long amount, String currency, String idempotencyKey) {
}
