package com.duytoan.imajicoffee.imaji_coffee_be.services.payment;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentResponseDto;

/**
 * Payment interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IPaymentService {
    /**
     * Create payment intent info for stripe
     * @param paymentIntentRequestDto -> intent dto
     * @return Payment intent response dto
     */
    PaymentIntentResponseDto createPaymentIntent(PaymentIntentRequestDto paymentIntentRequestDto);

    String getClientSecret(String paymentIntentId);

    void validateCardPaymentConfiguration();
}
