package com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.*;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.payment.PaymentWebhookEvent;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoUsage;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.payment.PaymentWebhookEventRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.impl.AddressServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.IMailService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.IOrderService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.payment.IPaymentService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final String CURRENCY = "USD";
    private static final Set<OrderStatus> CANCELLABLE_STATES = Set.of(
            OrderStatus.PENDING,
            OrderStatus.PAID,
            OrderStatus.PROCESSING
    );
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.PROCESSING, OrderStatus.PAID, OrderStatus.PAYMENT_FAILED, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED, OrderStatus.REFUNDED),
            OrderStatus.PROCESSING, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED, OrderStatus.REFUNDED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED, OrderStatus.REFUNDED),
            OrderStatus.DELIVERED, Set.of(OrderStatus.REFUNDED),
            OrderStatus.PAYMENT_FAILED, Set.of(),
            OrderStatus.CANCELLED, Set.of(),
            OrderStatus.REFUNDED, Set.of()
    );

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final OrderItemServiceImpl orderItemService;
    private final IPaymentService paymentService;
    private final ShipRepository shipRepository;
    private final UserRepository userRepository;
    private final AddressServiceImpl addressServiceImpl;
    private final IMailService mailService;
    private final PromoRepository promoRepository;
    private final PromoUsageRepository promoUsageRepository;
    private final PaymentWebhookEventRepository paymentWebhookEventRepository;
    private final ProductRepository productRepository;
    private final PromoPricingService promoPricingService;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, Long userId) throws MessagingException {
        validateOrderRequest(request);
        String paymentMethod = request.paymentMethod().toLowerCase(Locale.ROOT);
        if ("card".equals(paymentMethod)) {
            paymentService.validateCardPaymentConfiguration();
        }
        List<OrderItemDto> normalizedItems = normalizeOrderItems(request.items());
        validateProductsBeforeOrderCreation(normalizedItems);

        Optional<Order> existing = orderRepository.findByUserIdAndCheckoutRequestId(userId, request.idempotencyKey());
        if (existing.isPresent()) {
            Order idempotentOrder = existing.get();
            String existingClientSecret = "card".equalsIgnoreCase(idempotentOrder.getPaymentMethod())
                    ? paymentService.getClientSecret(idempotentOrder.getPaymentIntentId())
                    : null;
            return mapToOrderResponseDto(idempotentOrder, existingClientSecret);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));
        Ship shipMethod = shipRepository.findById(request.shipMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping", "shipMethodId", request.shipMethodId().toString()));

        Order order = mapToOrder(request, user, shipMethod);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> savedItems = orderItemService.saveOrderItems(savedOrder, normalizedItems);
        savedOrder.setOrderItems(new LinkedHashSet<>(savedItems));
        PricingBreakdown pricing = calculatePricing(savedItems, shipMethod, request.couponCode(), userId, true);

        savedOrder.setTaxAmount(pricing.taxAmount());
        savedOrder.setShippingAmount(pricing.shippingAmount());
        savedOrder.setDiscountAmount(pricing.discountAmount());
        savedOrder.setTotalAmount(pricing.totalAmount());
        savedOrder.setUpdatedAt(Instant.now());
        savedOrder.setUpdatedBy(user.getUsername());
        savedOrder = orderRepository.save(savedOrder);

        if (pricing.promoUsed() != null) {
            consumePromoUsage(pricing.promoUsed(), userId, savedOrder.getOrderId(), user.getUsername());
        }

        String clientSecret = null;
        if ("card".equals(paymentMethod)) {
            var paymentIntentResponse = paymentService.createPaymentIntent(
                    new PaymentIntentRequestDto(
                            savedOrder.getOrderId(),
                            pricing.totalAmount().multiply(new BigDecimal("100")).longValue(),
                            CURRENCY.toLowerCase(Locale.ROOT),
                            request.idempotencyKey()
                    )
            );
            savedOrder.setPaymentIntentId(paymentIntentResponse.paymentIntentId());
            orderRepository.save(savedOrder);
            clientSecret = paymentIntentResponse.clientSecret();
        }

        if ("cod".equals(paymentMethod)) {
            sendConfirmationEmailIfNeeded(savedOrder);
        }

        AddressDto address = getAddressDto(request, userId);
        addressServiceImpl.saveAddressForOder(address, userId);

        return mapToOrderResponseDto(savedOrder, clientSecret);
    }

    private static AddressDto getAddressDto(OrderRequestDto request, Long userId) {
        AddressDto requestAddress = request.shippingAddress();
        AddressDto address = new AddressDto(
                null,
                userId,
                requestAddress.name(),
                requestAddress.country(),
                requestAddress.province(),
                requestAddress.city(),
                requestAddress.street(),
                requestAddress.postalCode(),
                requestAddress.apartment(),
                requestAddress.phoneNumber(),
                requestAddress.isDefault()
        );
        return address;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrderForPaypal(OrderRequestDto request, Long userId) throws MessagingException {
        return createOrder(request, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrder(Long orderId, Long requesterUserId, boolean isAdmin) {
        Order order = loadAuthorizedOrder(orderId, requesterUserId, isAdmin);
        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponseDto)
                .toList();
        return new OrderDetailResponseDto(
                order.getOrderId(),
                order.getStatus().name(),
                order.getPaymentStatus() != null ? order.getPaymentStatus().name() : PaymentStatus.UNPAID.name(),
                order.getTotalAmount(),
                order.getTaxAmount(),
                order.getShippingAmount(),
                order.getDiscountAmount(),
                order.getEmail(),
                order.getShippingAddress(),
                order.getShippingMethod(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountOrderDetailResponseDto> getAccountOrders(Long userId, String status) {
        return orderRepository.findByUserId(userId).stream()
                .filter(order -> status == null || status.isBlank()
                        || order.getStatus().name().equalsIgnoreCase(status))
                .map(this::mapToAccountOrderDetail)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatusByAdmin(Long orderId, OrderStatus status, String updatedBy) {
        if (status == OrderStatus.PAID || status == OrderStatus.PAYMENT_FAILED || status == OrderStatus.REFUNDED) {
            throw new AccessDeniedException("Payment statuses can only be changed by webhook events");
        }

        Order order = orderRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId.toString()));

        validateTransition(order.getStatus(), status);
        if (status == OrderStatus.CANCELLED) {
            releaseStockIfNeeded(order);
            if ("cod".equalsIgnoreCase(order.getPaymentMethod()) && order.getPaymentStatus() != PaymentStatus.PAID) {
                restorePromoUsage(order.getOrderId(), updatedBy);
            }
        }

        order.setStatus(status);
        order.setUpdatedBy(updatedBy);
        order.setUpdatedAt(Instant.now());
        return mapToOrderResponseDto(orderRepository.save(order), null);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, Long requesterUserId, boolean isAdmin, String updatedBy) {
        Order order = loadAuthorizedOrderForUpdate(orderId, requesterUserId, isAdmin);
        if (!CANCELLABLE_STATES.contains(order.getStatus())) {
            throw new IllegalArgumentException("Order cannot be cancelled in status: " + order.getStatus());
        }

        validateTransition(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedBy(updatedBy);
        order.setUpdatedAt(Instant.now());
        releaseStockIfNeeded(order);
        if ("cod".equalsIgnoreCase(order.getPaymentMethod()) && order.getPaymentStatus() != PaymentStatus.PAID) {
            restorePromoUsage(order.getOrderId(), updatedBy);
        }
        return mapToOrderResponseDto(orderRepository.save(order), null);
    }

    @Override
    @Transactional
    public OrderResponseDto recordCodPaymentCollection(Long orderId, String updatedBy) {
        Order order = orderRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId.toString()));

        if (!"cod".equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalArgumentException("Payment collection can only be recorded for COD orders");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return mapToOrderResponseDto(order, null);
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("COD payment can only be collected after the order is delivered");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setUpdatedBy(updatedBy);
        order.setUpdatedAt(Instant.now());
        return mapToOrderResponseDto(orderRepository.save(order), null);
    }

    @Override
    @Transactional
    public void handleStripeWebhookEvent(String eventId, String eventType, String paymentIntentId, String externalPaymentId) {
        if (isWebhookDuplicate("STRIPE", eventId)) {
            return;
        }
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            markWebhookProcessed("STRIPE", eventId);
            return;
        }

        Order order = orderRepository.findByPaymentIntentId(paymentIntentId)
                .orElse(null);
        if (order == null) {
            markWebhookProcessed("STRIPE", eventId);
            return;
        }

        Order lockedOrder = orderRepository.findByOrderIdForUpdate(order.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", order.getOrderId().toString()));
        applyPaymentEventTransition(lockedOrder, eventType, externalPaymentId);
        markWebhookProcessed("STRIPE", eventId);
    }

    @Override
    @Transactional
    public void handlePayPalWebhookEvent(String eventId, String eventType, Long orderId, String externalPaymentId) {
        if (isWebhookDuplicate("PAYPAL", eventId)) {
            return;
        }
        if (orderId == null) {
            markWebhookProcessed("PAYPAL", eventId);
            return;
        }

        Order order = orderRepository.findByOrderIdForUpdate(orderId)
                .orElse(null);
        if (order == null) {
            markWebhookProcessed("PAYPAL", eventId);
            return;
        }
        if ("cod".equalsIgnoreCase(order.getPaymentMethod())) {
            markWebhookProcessed("PAYPAL", eventId);
            return;
        }

        applyPaymentEventTransition(order, eventType, externalPaymentId);
        markWebhookProcessed("PAYPAL", eventId);
    }

    private Order loadAuthorizedOrder(Long orderId, Long requesterUserId, boolean isAdmin) {
        if (isAdmin) {
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId.toString()));
        }
        return orderRepository.findByOrderIdAndUserId(orderId, requesterUserId)
                .orElseThrow(() -> new AccessDeniedException("Not authorized to access this order"));
    }

    private Order loadAuthorizedOrderForUpdate(Long orderId, Long requesterUserId, boolean isAdmin) {
        Order order = orderRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId.toString()));
        if (!isAdmin && !Objects.equals(order.getUserId(), requesterUserId)) {
            throw new AccessDeniedException("Not authorized to modify this order");
        }
        return order;
    }

    private PricingBreakdown calculatePricing(List<OrderItem> items, Ship shipMethod, String couponCode, Long userId, boolean checkUsage) {
        BigDecimal subtotal = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = shipMethod.getPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO;
        Promo appliedPromo = null;

        if (couponCode != null && !couponCode.isBlank()) {
            Promo promo = (checkUsage
                    ? promoRepository.findByCodeForUpdate(couponCode.trim())
                    : promoRepository.findByCodeIgnoreCase(couponCode.trim()))
                    .orElseThrow(() -> new IllegalArgumentException("Promo code is invalid"));

            PromoPricingSnapshot evaluation = promoPricingService.evaluate(
                    promo,
                    userId,
                    items,
                    subtotal,
                    shipping,
                    checkUsage
            );
            if (!evaluation.accepted()) {
                throw new IllegalArgumentException(evaluation.message());
            }
            discount = evaluation.discountAmount();
            shipping = evaluation.shippingAmount();
            appliedPromo = promo;
        }

        BigDecimal total = subtotal.add(tax).add(shipping).subtract(discount).setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return new PricingBreakdown(subtotal, tax, shipping, discount, total, appliedPromo);
    }

    private void consumePromoUsage(Promo promo, Long userId, Long orderId, String createdBy) {
        if (promo == null) {
            return;
        }
        Promo lockedPromo = promoRepository.findByPromoIdForUpdate(promo.getPromoId())
                .orElseThrow(() -> new ResourceNotFoundException("Promo", "promoId", promo.getPromoId().toString()));
        Integer currentUsageCount = lockedPromo.getUsageCount() == null ? 0 : lockedPromo.getUsageCount();
        lockedPromo.setUsageCount(currentUsageCount + 1);
        lockedPromo.setUpdatedAt(Instant.now());
        lockedPromo.setUpdatedBy(createdBy);
        promoRepository.save(lockedPromo);

        PromoUsage promoUsage = new PromoUsage();
        promoUsage.setPromo(lockedPromo);
        promoUsage.setUserId(userId);
        promoUsage.setOrderId(orderId);
        promoUsage.setUsedAt(Instant.now());
        promoUsage.setCreatedBy(createdBy);
        promoUsageRepository.save(promoUsage);
    }

    private void restorePromoUsage(Long orderId, String updatedBy) {
        PromoUsage usage = promoUsageRepository.findByOrderId(orderId).orElse(null);
        if (usage == null) {
            return;
        }
        Promo promo = usage.getPromo();
        if (promo != null) {
            Promo lockedPromo = promoRepository.findByPromoIdForUpdate(promo.getPromoId())
                    .orElse(promo);
            Integer currentUsageCount = lockedPromo.getUsageCount() == null ? 0 : lockedPromo.getUsageCount();
            if (currentUsageCount > 0) {
                lockedPromo.setUsageCount(currentUsageCount - 1);
                lockedPromo.setUpdatedAt(Instant.now());
                lockedPromo.setUpdatedBy(updatedBy);
                promoRepository.save(lockedPromo);
            }
        }
        promoUsageRepository.delete(usage);
    }

    @Transactional(readOnly = true)
    public PricingBreakdown quotePricingForCart(List<OrderItem> items, Ship shipMethod, String couponCode, Long userId) {
        return calculatePricing(items, shipMethod, couponCode, userId, false);
    }

    public record PricingBreakdown(
            BigDecimal subtotalAmount,
            BigDecimal taxAmount,
            BigDecimal shippingAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            Promo promoUsed
    ) {
    }

    private void validateOrderRequest(OrderRequestDto request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        if (request.shippingAddress() == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }
    }

    private void validateProductsBeforeOrderCreation(List<OrderItemDto> items) {
        for (OrderItemDto item : items) {
            var product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", item.productId().toString()));

            if (Boolean.FALSE.equals(product.getIsAvailableAtWeb())) {
                throw new IllegalArgumentException("Product is unavailable: " + product.getName());
            }
            if (item.quantity() > product.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }
        }
    }

    private List<OrderItemDto> normalizeOrderItems(List<OrderItemDto> items) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItemDto item : items) {
            if (item.productId() == null) {
                throw new IllegalArgumentException("Product id is required");
            }
            if (item.quantity() == null || item.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            quantities.merge(item.productId(), item.quantity(), Integer::sum);
        }

        return quantities.entrySet().stream()
                .map(entry -> new OrderItemDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new IllegalArgumentException("Invalid order status transition: " + current + " -> " + next);
        }
    }

    private void applyPaymentEventTransition(Order order, String eventType, String externalPaymentId) {
        if (eventType == null) {
            return;
        }

        String type = eventType.toLowerCase(Locale.ROOT);
        if (type.contains("succeeded") || type.contains("completed")) {
            markPaid(order, externalPaymentId);
            return;
        }

        if (type.contains("failed") || type.contains("denied")) {
            markPaymentFailed(order, externalPaymentId);
            return;
        }

        if (type.contains("canceled") || type.contains("cancelled") || type.contains("expired") || type.contains("voided")) {
            markCancelled(order, externalPaymentId);
            return;
        }

        if (type.contains("refunded") || type.contains("reversed")) {
            markRefunded(order, externalPaymentId);
        }
    }

    private void markPaid(Order order, String externalPaymentId) {
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.PROCESSING
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {
            sendConfirmationEmailIfNeeded(order);
            return;
        }
        validateTransition(order.getStatus(), OrderStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setExternalPaymentId(externalPaymentId);
        order.setUpdatedBy("WEBHOOK");
        order.setUpdatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);
        sendConfirmationEmailIfNeeded(savedOrder);
    }

    private void sendConfirmationEmailIfNeeded(Order order) {
        if (Boolean.TRUE.equals(order.getConfirmationEmailSent())) {
            return;
        }
        try {
            mailService.sendOrderInfoToEmail(order);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send order confirmation email", e);
        }
        order.setConfirmationEmailSent(true);
        order.setConfirmationEmailSentAt(Instant.now());
        order.setUpdatedBy("WEBHOOK");
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
    }

    private void markPaymentFailed(Order order, String externalPaymentId) {
        if (order.getStatus() == OrderStatus.PAYMENT_FAILED || order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        if (order.getStatus() == OrderStatus.PENDING) {
            validateTransition(order.getStatus(), OrderStatus.PAYMENT_FAILED);
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setExternalPaymentId(externalPaymentId);
            order.setUpdatedBy("WEBHOOK");
            order.setUpdatedAt(Instant.now());
            releaseStockIfNeeded(order);
            orderRepository.save(order);
        }
    }

    private void markCancelled(Order order, String externalPaymentId) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.PROCESSING) {
            validateTransition(order.getStatus(), OrderStatus.CANCELLED);
            order.setStatus(OrderStatus.CANCELLED);
            order.setExternalPaymentId(externalPaymentId);
            order.setUpdatedBy("WEBHOOK");
            order.setUpdatedAt(Instant.now());
            releaseStockIfNeeded(order);
            orderRepository.save(order);
        }
    }

    private void markRefunded(Order order, String externalPaymentId) {
        if (order.getStatus() == OrderStatus.REFUNDED) {
            return;
        }
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == OrderStatus.PAID
                || currentStatus == OrderStatus.PROCESSING
                || currentStatus == OrderStatus.SHIPPED
                || currentStatus == OrderStatus.DELIVERED) {
            validateTransition(currentStatus, OrderStatus.REFUNDED);
            order.setStatus(OrderStatus.REFUNDED);
            order.setExternalPaymentId(externalPaymentId);
            order.setUpdatedBy("WEBHOOK");
            order.setUpdatedAt(Instant.now());
            if (currentStatus != OrderStatus.DELIVERED) {
                releaseStockIfNeeded(order);
            }
            orderRepository.save(order);
        }
    }

    private void releaseStockIfNeeded(Order order) {
        if (Boolean.TRUE.equals(order.getStockReleased())) {
            return;
        }
        for (OrderItem item : order.getOrderItems()) {
            orderItemService.restoreProductStock(item.getProductId(), item.getQuantity());
        }
        order.setStockReleased(true);
    }

    private boolean isWebhookDuplicate(String provider, String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        return paymentWebhookEventRepository.existsByProviderAndEventId(provider, eventId);
    }

    private void markWebhookProcessed(String provider, String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        if (paymentWebhookEventRepository.existsByProviderAndEventId(provider, eventId)) {
            return;
        }
        PaymentWebhookEvent event = new PaymentWebhookEvent();
        event.setProvider(provider);
        event.setEventId(eventId);
        event.setCreatedAt(Instant.now());
        paymentWebhookEventRepository.save(event);
    }

    private AccountOrderDetailResponseDto mapToAccountOrderDetail(Order order) {
        AccountOrderDetailResponseDto dto = new AccountOrderDetailResponseDto();
        BeanUtils.copyProperties(order, dto);
        dto.setItems(order.getOrderItems().size());
        dto.setAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        if (order.getPaymentStatus() != null) {
            dto.setPaymentStatus(order.getPaymentStatus());
        }
        return dto;
    }

    private OrderItemResponseDto mapToOrderItemResponseDto(OrderItem item) {
        return new OrderItemResponseDto(
                item.getProductId(),
                item.getProductName(),
                item.getProductImg(),
                item.getProductCategory(),
                item.getPrice(),
                item.getQuantity()
        );
    }

    private OrderResponseDto mapToOrderResponseDto(Order order, String clientSecret) {
        BigDecimal subtotal = order.getOrderItems().stream()
                .filter(item -> item.getPrice() != null && item.getQuantity() != null)
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new OrderResponseDto(
                order.getOrderId(),
                order.getStatus().name(),
                order.getPaymentStatus() != null ? order.getPaymentStatus().name() : PaymentStatus.UNPAID.name(),
                clientSecret,
                subtotal,
                order.getTaxAmount(),
                order.getShippingAmount(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getCurrency()
        );
    }

    private Order mapToOrder(OrderRequestDto request, User user, Ship shipMethod) {
        try {
            Order order = new Order();
            String shippingJson = objectMapper.writeValueAsString(request.shippingAddress());
            order.setUserId(user.getUserId());
            order.setEmail(user.getEmail());
            order.setShippingAddress(shippingJson);
            order.setTotalAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            order.setTaxAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            order.setShippingAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            order.setDiscountAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            order.setCurrency(CURRENCY);
            order.setStatus(OrderStatus.PENDING);
            order.setShippingMethod(shipMethod.getMethodName() + " " + shipMethod.getExpectedArrival());
            order.setPaymentMethod(request.paymentMethod().toLowerCase(Locale.ROOT));
            order.setCheckoutRequestId(request.idempotencyKey());
            order.setStockReleased(false);
            order.setCreatedBy(user.getUsername() != null ? user.getUsername() : "SYSTEM");
            order.setCreatedAt(Instant.now());
            return order;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map order request", e);
        }
    }

}
