package com.duytoan.imajicoffee.imaji_coffee_be.repository.payment;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.payment.PaymentWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentWebhookEventRepository extends JpaRepository<PaymentWebhookEvent, Long> {
    boolean existsByProviderAndEventId(String provider, String eventId);
}
