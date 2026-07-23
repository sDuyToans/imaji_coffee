package com.duytoan.imajicoffee.imaji_coffee_be.entities.order;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_orders_user_checkout_request", columnNames = {"user_id", "checkout_request_id"})
        }
)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Lob
    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "external_payment_id", length = 100)
    private String externalPaymentId;

    @Column(name = "checkout_request_id", length = 100)
    private String checkoutRequestId;

    @Column(name = "stock_released", nullable = false)
    private Boolean stockReleased = false;

    @Column(name = "confirmation_email_sent", nullable = false)
    private Boolean confirmationEmailSent = false;

    @Column(name = "confirmation_email_sent_at")
    private Instant confirmationEmailSentAt;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    private String shippingMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

}
