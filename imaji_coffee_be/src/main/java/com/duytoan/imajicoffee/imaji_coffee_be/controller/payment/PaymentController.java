package com.duytoan.imajicoffee.imaji_coffee_be.controller.payment;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    /**
     * Create payment intent -> stripe request
     * @param paymentIntentRequestDto -> payment intent dto obj
     * @return -> res entity
     */
    @PostMapping("/create-payment-intent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentIntentResponseDto> createPaymentIntent(@RequestBody PaymentIntentRequestDto paymentIntentRequestDto) {
        PaymentIntentResponseDto paymentIntentResponseDto = paymentService.createPaymentIntent(paymentIntentRequestDto);
        return ResponseEntity.ok(paymentIntentResponseDto);
    }
}
