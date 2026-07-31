package com.duytoan.imajicoffee.imaji_coffee_be.services.payment.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.PaymentProcessingException;
import com.duytoan.imajicoffee.imaji_coffee_be.services.payment.IPaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implemented PaymentService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
public class PaymentServiceImpl implements IPaymentService {
    @Value("${stripe.secretKey:dummy}")
    private String stripeSecretKey;

    /**
     * Create payment intent info for stripe
     * @param requestDto -> intent dto
     * @return Payment intent response dto
     */
    @Override
    public PaymentIntentResponseDto createPaymentIntent(PaymentIntentRequestDto requestDto) {
        validateCardPaymentConfiguration();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(requestDto.amount())
                    .setCurrency(requestDto.currency())
                    .addPaymentMethodType("card")
                    .putMetadata("orderId", String.valueOf(requestDto.orderId()))
                    .build();
            RequestOptions requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(requestDto.idempotencyKey())
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);
            return new PaymentIntentResponseDto(paymentIntent.getId(), paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new PaymentProcessingException("Card payment is temporarily unavailable. Please try again.", e);
        }
    }

    @Override
    public String getClientSecret(String paymentIntentId) {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            return null;
        }
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            throw new PaymentProcessingException("Failed to retrieve payment session", e);
        }
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            return null;
        }
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            throw new PaymentProcessingException("Failed to retrieve payment intent", e);
        }
    }

    @Override
    public void validateCardPaymentConfiguration() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank() || "dummy".equalsIgnoreCase(stripeSecretKey)) {
            throw new PaymentProcessingException("Card payment is not configured. Please set STRIPE_SECRET_KEY.");
        }
    }
}
