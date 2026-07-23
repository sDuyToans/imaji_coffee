package com.duytoan.imajicoffee.imaji_coffee_be.entities.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "payment_webhook_event",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_payment_webhook_provider_event", columnNames = {"provider", "event_id"})
        }
)
public class PaymentWebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "event_id", nullable = false, length = 120)
    private String eventId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
