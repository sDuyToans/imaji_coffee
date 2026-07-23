package com.duytoan.imajicoffee.imaji_coffee_be.services.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.*;
import jakarta.mail.MessagingException;

import java.util.List;

/**
 * OrderItem interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IOrderService {
    /**
     * Create new order
     * @param orderRequestDto -> order object
     * @param userId -> long
     * @return order request dto
     * @throws MessagingException -> throw mess exception
     */
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto, Long userId) throws MessagingException;

    /**
     * Update order status
     * @param orderId -> long
     * @param status -> status
     * @param updatedBy -> actor
     * @return -> order object updated
     */
    OrderResponseDto updateOrderStatusByAdmin(Long orderId, com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus status, String updatedBy);

    /**
     * Get order by id
     * @param orderId -> long
     * @return -> order detail dto
     */
    OrderDetailResponseDto getOrder(Long orderId, Long requesterUserId, boolean isAdmin);

    /**
     * Get list account's orders by userId
     * @param userId -> long
     * @param status -> optional status filter
     * @return -> list account order detail dto
     */
    List<AccountOrderDetailResponseDto> getAccountOrders(Long userId, String status);

    /**
     * Create order for PayPal method
     * @param orderRequestDto -> request dto
     * @param userId -> long
     * @return order dto
     * @throws MessagingException -> mess exception
     */
    OrderResponseDto createOrderForPaypal(OrderRequestDto orderRequestDto, Long userId) throws MessagingException;

    OrderResponseDto cancelOrder(Long orderId, Long requesterUserId, boolean isAdmin, String updatedBy);

    OrderResponseDto recordCodPaymentCollection(Long orderId, String updatedBy);

    void handleStripeWebhookEvent(
            String eventId,
            String eventType,
            String paymentIntentId,
            String externalPaymentId
    );

    void handlePayPalWebhookEvent(
            String eventId,
            String eventType,
            Long orderId,
            String externalPaymentId
    );
}
