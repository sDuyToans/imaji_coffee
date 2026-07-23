package com.duytoan.imajicoffee.imaji_coffee_be.entities.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "promo_usage",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_promo_usage_promo_user_order", columnNames = {"promo_id", "user_id", "order_id"})
        }
)
public class PromoUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_usage_id", nullable = false)
    private Long promoUsageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "used_at", nullable = false)
    private Instant usedAt;

    @Column(name = "created_by", nullable = false, length = 20)
    private String createdBy;
}
