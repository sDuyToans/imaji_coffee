package com.duytoan.imajicoffee.imaji_coffee_be.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.AccountOrderDetailResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderItemDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoUsage;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.payment.PaymentWebhookEventRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.impl.AddressServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.IMailService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.OrderItemServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.OrderServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.payment.IPaymentService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private OrderItemServiceImpl orderItemService;
    @Mock
    private IPaymentService paymentService;
    @Mock
    private ShipRepository shipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressServiceImpl addressServiceImpl;
    @Mock
    private IMailService mailService;
    @Mock
    private PromoRepository promoRepository;
    @Mock
    private PaymentWebhookEventRepository paymentWebhookEventRepository;
    @Mock
    private PromoUsageRepository promoUsageRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromoPricingService promoPricingService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Ship ship;
    private AddressDto addressDto;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(10L);
        user.setUsername("tester");
        user.setEmail("tester@example.com");

        ship = new Ship();
        ship.setMethodId(1L);
        ship.setMethodName("Standard");
        ship.setExpectedArrival("3 days");
        ship.setPrice(new BigDecimal("5.00"));

        addressDto = new AddressDto(
                1L,
                10L,
                "Test User",
                "US",
                "CA",
                "SF",
                "Market St",
                "94105",
                "Apt 1",
                "+14155550123",
                false
        );
        product = new Product();
        product.setProductId(101L);
        product.setName("Iced Latte");
        product.setQuantity(100);
        product.setIsAvailableAtWeb(true);

        lenient().when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            if (o.getOrderId() == null) {
                o.setOrderId(1L);
            }
            return o;
        });
    }

    @Test
    void createOrder_recalculatesTotalsFromDatabaseSnapshot() throws Exception {
        OrderRequestDto request = new OrderRequestDto(
                "card",
                1L,
                addressDto,
                null,
                "checkout-key-1",
                List.of(new OrderItemDto(101L, 2))
        );

        Order persistedOrder = baseOrder(1L, OrderStatus.PENDING);
        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setPrice(new BigDecimal("12.50"));
        item.setQuantity(2);
        item.setOrder(persistedOrder);
        persistedOrder.setOrderItems(Set.of(item));

        when(orderRepository.findByUserIdAndCheckoutRequestId(10L, "checkout-key-1")).thenReturn(Optional.empty());
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(shipRepository.findById(1L)).thenReturn(Optional.of(ship));
        when(objectMapper.writeValueAsString(any(AddressDto.class))).thenReturn("{\"address\":true}");
        when(orderItemService.saveOrderItems(any(Order.class), anyList())).thenReturn(List.of(item));
        when(paymentService.createPaymentIntent(any())).thenReturn(new PaymentIntentResponseDto("pi_1", "cs_1"));

        OrderResponseDto response = orderService.createOrder(request, 10L);

        assertThat(response.subtotalAmount()).isEqualByComparingTo("25.00");
        assertThat(response.taxAmount()).isEqualByComparingTo("2.50");
        assertThat(response.shippingAmount()).isEqualByComparingTo("5.00");
        assertThat(response.totalAmount()).isEqualByComparingTo("32.50");
        assertThat(response.clientSecret()).isEqualTo("cs_1");
        verify(mailService, never()).sendOrderInfoToEmail(any(Order.class));
    }

    @Test
    void createOrder_rejectsInvalidCoupon() {
        OrderRequestDto request = new OrderRequestDto(
                "card",
                1L,
                addressDto,
                "BADCODE",
                "checkout-key-2",
                List.of(new OrderItemDto(101L, 1))
        );

        Order order = baseOrder(1L, OrderStatus.PENDING);
        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setPrice(new BigDecimal("10.00"));
        item.setQuantity(1);
        item.setOrder(order);

        when(orderRepository.findByUserIdAndCheckoutRequestId(10L, "checkout-key-2")).thenReturn(Optional.empty());
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(shipRepository.findById(1L)).thenReturn(Optional.of(ship));
        when(orderItemService.saveOrderItems(any(Order.class), anyList())).thenReturn(List.of(item));
        when(promoRepository.findByCodeForUpdate("BADCODE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Promo code is invalid");
    }

    @Test
    void createOrder_withPromoConsumesUsage() throws Exception {
        OrderRequestDto request = new OrderRequestDto(
                "card",
                1L,
                addressDto,
                "SAVE10",
                "checkout-promo-1",
                List.of(new OrderItemDto(101L, 2))
        );

        Order persistedOrder = baseOrder(31L, OrderStatus.PENDING);
        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setPrice(new BigDecimal("12.50"));
        item.setQuantity(2);
        item.setProductCategory("coffee_baverage");
        item.setOrder(persistedOrder);
        persistedOrder.setOrderItems(Set.of(item));

        Promo promo = new Promo();
        promo.setPromoId(50L);
        promo.setCode("SAVE10");
        promo.setDiscountType("fixed");
        promo.setDiscountValue(new BigDecimal("5.00"));
        promo.setUsageCount(0);

        when(orderRepository.findByUserIdAndCheckoutRequestId(10L, "checkout-promo-1")).thenReturn(Optional.empty());
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(shipRepository.findById(1L)).thenReturn(Optional.of(ship));
        when(objectMapper.writeValueAsString(any(AddressDto.class))).thenReturn("{\"address\":true}");
        when(orderItemService.saveOrderItems(any(Order.class), anyList())).thenReturn(List.of(item));
        when(promoRepository.findByCodeForUpdate("SAVE10")).thenReturn(Optional.of(promo));
        when(promoPricingService.evaluate(any(), any(), anyList(), any(), any(), anyBoolean()))
                .thenReturn(new PromoPricingSnapshot(
                        true,
                        "Promo code applied successfully",
                        "eligible",
                        new BigDecimal("5.00"),
                        new BigDecimal("5.00"),
                        promo,
                        new BigDecimal("25.00")
                ));
        when(promoRepository.findByPromoIdForUpdate(50L)).thenReturn(Optional.of(promo));
        when(paymentService.createPaymentIntent(any())).thenReturn(new PaymentIntentResponseDto("pi_1", "cs_1"));

        OrderResponseDto response = orderService.createOrder(request, 10L);

        assertThat(response.discountAmount()).isEqualByComparingTo("5.00");
        verify(promoUsageRepository).save(any());
        verify(promoRepository, atLeastOnce()).save(any(Promo.class));
    }

    @Test
    void getOrder_nonOwnerDenied() {
        when(orderRepository.findByOrderIdAndUserId(1L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(1L, 10L, false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void createOrder_duplicateCheckoutReturnsExistingOrder() throws Exception {
        Order existingOrder = baseOrder(22L, OrderStatus.PENDING);
        existingOrder.setPaymentMethod("card");
        existingOrder.setPaymentIntentId("pi_existing");
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setPrice(new BigDecimal("8.00"));
        item.setQuantity(1);
        existingOrder.setOrderItems(Set.of(item));
        existingOrder.setTaxAmount(new BigDecimal("0.80"));
        existingOrder.setShippingAmount(new BigDecimal("0.00"));
        existingOrder.setDiscountAmount(new BigDecimal("0.00"));
        existingOrder.setTotalAmount(new BigDecimal("8.80"));
        existingOrder.setCurrency("USD");

        OrderRequestDto request = new OrderRequestDto(
                "card",
                1L,
                addressDto,
                null,
                "same-key",
                List.of(new OrderItemDto(1L, 1))
        );

        when(orderRepository.findByUserIdAndCheckoutRequestId(10L, "same-key")).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(paymentService.getClientSecret("pi_existing")).thenReturn("cs_existing");

        OrderResponseDto response = orderService.createOrder(request, 10L);

        verify(orderItemService, never()).saveOrderItems(any(), anyList());
        assertThat(response.orderId()).isEqualTo(22L);
        assertThat(response.clientSecret()).isEqualTo("cs_existing");
    }

    @Test
    void handleStripeWebhookEvent_successMarksOrderPaid() throws Exception {
        Order order = baseOrder(1L, OrderStatus.PENDING);
        order.setPaymentIntentId("pi_1");

        when(paymentWebhookEventRepository.existsByProviderAndEventId("STRIPE", "evt_1")).thenReturn(false);
        when(orderRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(order));

        orderService.handleStripeWebhookEvent("evt_1", "payment_intent.succeeded", "pi_1", "ch_1");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getConfirmationEmailSent()).isTrue();
        verify(mailService).sendOrderInfoToEmail(order);
        verify(paymentWebhookEventRepository).save(any());
    }

    @Test
    void handleStripeWebhookEvent_duplicateEventDoesNotSendDuplicateEmail() throws Exception {
        Order order = baseOrder(11L, OrderStatus.PENDING);
        order.setPaymentIntentId("pi_11");

        when(paymentWebhookEventRepository.existsByProviderAndEventId("STRIPE", "evt_same"))
                .thenReturn(false, true);
        when(orderRepository.findByPaymentIntentId("pi_11")).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderIdForUpdate(11L)).thenReturn(Optional.of(order));

        orderService.handleStripeWebhookEvent("evt_same", "payment_intent.succeeded", "pi_11", "ch_11");
        orderService.handleStripeWebhookEvent("evt_same", "payment_intent.succeeded", "pi_11", "ch_11");

        verify(mailService, times(1)).sendOrderInfoToEmail(order);
    }

    @Test
    void handleStripeWebhookEvent_paidOrderWithoutEmailStillSendsOnce() throws Exception {
        Order order = baseOrder(12L, OrderStatus.PAID);
        order.setPaymentIntentId("pi_12");
        order.setConfirmationEmailSent(false);

        when(paymentWebhookEventRepository.existsByProviderAndEventId("STRIPE", "evt_12")).thenReturn(false);
        when(orderRepository.findByPaymentIntentId("pi_12")).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderIdForUpdate(12L)).thenReturn(Optional.of(order));

        orderService.handleStripeWebhookEvent("evt_12", "payment_intent.succeeded", "pi_12", "ch_12");

        verify(mailService, times(1)).sendOrderInfoToEmail(order);
        assertThat(order.getConfirmationEmailSent()).isTrue();
    }

    @Test
    void handleStripeWebhookEvent_failedPaymentReleasesStock() {
        Order order = baseOrder(2L, OrderStatus.PENDING);
        order.setPaymentIntentId("pi_2");
        order.setStockReleased(false);
        OrderItem item = new OrderItem();
        item.setProductId(99L);
        item.setQuantity(2);
        order.setOrderItems(Set.of(item));

        when(paymentWebhookEventRepository.existsByProviderAndEventId("STRIPE", "evt_2")).thenReturn(false);
        when(orderRepository.findByPaymentIntentId("pi_2")).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderIdForUpdate(2L)).thenReturn(Optional.of(order));

        orderService.handleStripeWebhookEvent("evt_2", "payment_intent.payment_failed", "pi_2", "ch_2");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        assertThat(order.getStockReleased()).isTrue();
        verify(orderItemService).restoreProductStock(99L, 2);
    }

    @Test
    void cancelOrder_ownerCanCancelAndReleaseStock() {
        Order order = baseOrder(3L, OrderStatus.PENDING);
        order.setUserId(10L);
        OrderItem item = new OrderItem();
        item.setProductId(8L);
        item.setQuantity(1);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findByOrderIdForUpdate(3L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto response = orderService.cancelOrder(3L, 10L, false, "10");

        assertThat(response.status()).isEqualTo("CANCELLED");
        verify(orderItemService).restoreProductStock(8L, 1);
    }

    @Test
    void handlePayPalWebhookEvent_skipsCodOrders() {
        Order order = baseOrder(13L, OrderStatus.PENDING);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);

        when(paymentWebhookEventRepository.existsByProviderAndEventId("PAYPAL", "evt_pp_1")).thenReturn(false);
        when(orderRepository.findByOrderIdForUpdate(13L)).thenReturn(Optional.of(order));

        orderService.handlePayPalWebhookEvent("evt_pp_1", "CHECKOUT.ORDER.APPROVED", 13L, "pp_1");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getPaymentStatus()).isEqualTo(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        verify(paymentWebhookEventRepository).save(any());
    }

    @Test
    void handleStripeWebhookEvent_refundMarksRefunded() {
        Order order = baseOrder(4L, OrderStatus.PAID);
        order.setPaymentIntentId("pi_4");
        OrderItem item = new OrderItem();
        item.setProductId(7L);
        item.setQuantity(1);
        order.setOrderItems(Set.of(item));

        when(paymentWebhookEventRepository.existsByProviderAndEventId("STRIPE", "evt_4")).thenReturn(false);
        when(orderRepository.findByPaymentIntentId("pi_4")).thenReturn(Optional.of(order));
        when(orderRepository.findByOrderIdForUpdate(4L)).thenReturn(Optional.of(order));

        orderService.handleStripeWebhookEvent("evt_4", "charge.refunded", "pi_4", "ch_4");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUNDED);
    }

    @Test
    void updateOrderStatusByAdmin_rejectsInvalidTransition() {
        Order order = baseOrder(5L, OrderStatus.PENDING);
        when(orderRepository.findByOrderIdForUpdate(5L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatusByAdmin(5L, OrderStatus.SHIPPED, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid order status transition");
    }

    @Test
    void createOrder_codDoesNotCallStripeAndStartsUnpaid() throws Exception {
        OrderRequestDto request = new OrderRequestDto(
                "cod",
                1L,
                addressDto,
                null,
                "checkout-cod-1",
                List.of(new OrderItemDto(101L, 2))
        );

        Order persistedOrder = baseOrder(1L, OrderStatus.PENDING);
        persistedOrder.setPaymentMethod("cod");
        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setPrice(new BigDecimal("12.50"));
        item.setQuantity(2);
        item.setOrder(persistedOrder);
        persistedOrder.setOrderItems(Set.of(item));

        when(orderRepository.findByUserIdAndCheckoutRequestId(10L, "checkout-cod-1")).thenReturn(Optional.empty());
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(shipRepository.findById(1L)).thenReturn(Optional.of(ship));
        when(objectMapper.writeValueAsString(any(AddressDto.class))).thenReturn("{\"address\":true}");
        when(orderItemService.saveOrderItems(any(Order.class), anyList())).thenReturn(List.of(item));

        OrderResponseDto response = orderService.createOrder(request, 10L);

        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.paymentStatus()).isEqualTo("UNPAID");
        assertThat(response.clientSecret()).isNull();
        assertThat(response.subtotalAmount()).isEqualByComparingTo("25.00");
        assertThat(response.taxAmount()).isEqualByComparingTo("2.50");
        assertThat(response.shippingAmount()).isEqualByComparingTo("5.00");
        assertThat(response.totalAmount()).isEqualByComparingTo("32.50");
        verify(paymentService, never()).createPaymentIntent(any());
        verify(mailService, times(1)).sendOrderInfoToEmail(any(Order.class));
    }

    @Test
    void createOrder_duplicateCodReturnsSameOrderAndEmailSentOnce() throws Exception {
        Order existingOrder = baseOrder(20L, OrderStatus.PENDING);
        existingOrder.setPaymentMethod("cod");
        existingOrder.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setPrice(new BigDecimal("12.50"));
        item.setQuantity(2);
        existingOrder.setOrderItems(Set.of(item));
        existingOrder.setTaxAmount(new BigDecimal("2.50"));
        existingOrder.setShippingAmount(new BigDecimal("5.00"));
        existingOrder.setDiscountAmount(BigDecimal.ZERO);
        existingOrder.setTotalAmount(new BigDecimal("32.50"));

        OrderRequestDto request = new OrderRequestDto(
                "cod",
                1L,
                addressDto,
                null,
                "cod-same-key",
                List.of(new OrderItemDto(101L, 2))
        );

        when(orderRepository.findByUserIdAndCheckoutRequestId(10L, "cod-same-key")).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));

        OrderResponseDto first = orderService.createOrder(request, 10L);
        OrderResponseDto second = orderService.createOrder(request, 10L);

        assertThat(first.orderId()).isEqualTo(second.orderId());
        verify(orderItemService, never()).saveOrderItems(any(), anyList());
        verify(paymentService, never()).createPaymentIntent(any());
        verify(mailService, never()).sendOrderInfoToEmail(any(Order.class));
        assertThat(first.paymentStatus()).isEqualTo("UNPAID");
    }

    @Test
    void updateOrderStatusByAdmin_canMoveCodFromPendingToProcessing() {
        Order order = baseOrder(6L, OrderStatus.PENDING);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        when(orderRepository.findByOrderIdForUpdate(6L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto response = orderService.updateOrderStatusByAdmin(6L, OrderStatus.PROCESSING, "admin");

        assertThat(response.status()).isEqualTo("PROCESSING");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void updateOrderStatusByAdmin_canProgressCodLifecycle() {
        Order order = baseOrder(7L, OrderStatus.PENDING);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        when(orderRepository.findByOrderIdForUpdate(7L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.updateOrderStatusByAdmin(7L, OrderStatus.PROCESSING, "admin");
        orderService.updateOrderStatusByAdmin(7L, OrderStatus.SHIPPED, "admin");
        OrderResponseDto delivered = orderService.updateOrderStatusByAdmin(7L, OrderStatus.DELIVERED, "admin");

        assertThat(delivered.status()).isEqualTo("DELIVERED");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void cancelOrder_codRestoresStockOnce() {
        Order order = baseOrder(8L, OrderStatus.PENDING);
        order.setUserId(10L);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        OrderItem item = new OrderItem();
        item.setProductId(8L);
        item.setQuantity(1);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findByOrderIdForUpdate(8L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(8L, 10L, false, "10");

        verify(orderItemService, times(1)).restoreProductStock(8L, 1);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getStockReleased()).isTrue();
    }

    @Test
    void recordCodPaymentCollection_marksDeliveredCodAsPaid() {
        Order order = baseOrder(9L, OrderStatus.DELIVERED);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        when(orderRepository.findByOrderIdForUpdate(9L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto response = orderService.recordCodPaymentCollection(9L, "admin");

        assertThat(response.paymentStatus()).isEqualTo("PAID");
        assertThat(order.getPaymentStatus()).isEqualTo(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.PAID);
    }

    @Test
    void recordCodPaymentCollection_rejectsNonCodOrders() {
        Order order = baseOrder(10L, OrderStatus.DELIVERED);
        order.setPaymentMethod("card");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.PAID);
        when(orderRepository.findByOrderIdForUpdate(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.recordCodPaymentCollection(10L, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment collection can only be recorded for COD orders");
    }

    @Test
    void recordCodPaymentCollection_rejectsBeforeDelivery() {
        Order order = baseOrder(11L, OrderStatus.PROCESSING);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        when(orderRepository.findByOrderIdForUpdate(11L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.recordCodPaymentCollection(11L, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("COD payment can only be collected after the order is delivered");
    }

    @Test
    void updateOrderStatusByAdmin_cancelCodRestoresPromoUsage() {
        Order order = baseOrder(14L, OrderStatus.PENDING);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        OrderItem item = new OrderItem();
        item.setProductId(8L);
        item.setQuantity(1);
        order.setOrderItems(Set.of(item));

        Promo promo = new Promo();
        promo.setPromoId(51L);
        promo.setCode("SAVE5");
        promo.setUsageCount(1);

        PromoUsage usage = new PromoUsage();
        usage.setPromo(promo);
        usage.setUserId(10L);
        usage.setOrderId(14L);

        when(orderRepository.findByOrderIdForUpdate(14L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(promoUsageRepository.findByOrderId(14L)).thenReturn(Optional.of(usage));
        when(promoRepository.findByPromoIdForUpdate(51L)).thenReturn(Optional.of(promo));

        orderService.updateOrderStatusByAdmin(14L, OrderStatus.CANCELLED, "admin");

        assertThat(promo.getUsageCount()).isEqualTo(0);
        verify(promoUsageRepository).delete(usage);
        verify(orderItemService).restoreProductStock(8L, 1);
    }

    @Test
    void cancelOrder_codRestoresPromoUsage() {
        Order order = baseOrder(12L, OrderStatus.PENDING);
        order.setUserId(10L);
        order.setPaymentMethod("cod");
        order.setPaymentStatus(com.duytoan.imajicoffee.imaji_coffee_be.enums.PaymentStatus.UNPAID);
        OrderItem item = new OrderItem();
        item.setProductId(8L);
        item.setQuantity(1);
        order.setOrderItems(Set.of(item));

        Promo promo = new Promo();
        promo.setPromoId(50L);
        promo.setCode("SAVE10");
        promo.setUsageCount(1);

        PromoUsage usage = new PromoUsage();
        usage.setPromo(promo);
        usage.setUserId(10L);
        usage.setOrderId(12L);

        when(orderRepository.findByOrderIdForUpdate(12L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(promoUsageRepository.findByOrderId(12L)).thenReturn(Optional.of(usage));
        when(promoRepository.findByPromoIdForUpdate(50L)).thenReturn(Optional.of(promo));

        orderService.cancelOrder(12L, 10L, false, "10");

        assertThat(promo.getUsageCount()).isEqualTo(0);
        verify(promoUsageRepository).delete(usage);
    }

    @Test
    void getAccountOrders_shouldFilterByStatusCaseInsensitively() {
        Order pendingOrder = baseOrder(50L, OrderStatus.PENDING);
        Order shippedOrder = baseOrder(51L, OrderStatus.SHIPPED);
        when(orderRepository.findByUserId(10L)).thenReturn(List.of(pendingOrder, shippedOrder));

        List<AccountOrderDetailResponseDto> result = orderService.getAccountOrders(10L, "pending");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderId()).isEqualTo(50L);
    }

    @Test
    void getAccountOrders_shouldReturnAll_whenStatusBlank() {
        Order pendingOrder = baseOrder(50L, OrderStatus.PENDING);
        Order shippedOrder = baseOrder(51L, OrderStatus.SHIPPED);
        when(orderRepository.findByUserId(10L)).thenReturn(List.of(pendingOrder, shippedOrder));

        List<AccountOrderDetailResponseDto> result = orderService.getAccountOrders(10L, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void cancelOrder_shouldRejectIneligibleStatus() {
        Order order = baseOrder(60L, OrderStatus.SHIPPED);
        order.setUserId(10L);
        when(orderRepository.findByOrderIdForUpdate(60L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(60L, 10L, false, "10"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order cannot be cancelled");
    }

    @Test
    void cancelOrder_shouldReject_whenUserDoesNotOwnOrder() {
        Order order = baseOrder(61L, OrderStatus.PENDING);
        order.setUserId(99L);
        when(orderRepository.findByOrderIdForUpdate(61L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(61L, 10L, false, "10"))
                .isInstanceOf(AccessDeniedException.class);
    }

    private Order baseOrder(Long orderId, OrderStatus status) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(10L);
        order.setEmail("tester@example.com");
        order.setShippingAddress("{}");
        order.setStatus(status);
        order.setPaymentMethod("card");
        order.setCurrency("USD");
        order.setTaxAmount(BigDecimal.ZERO);
        order.setShippingAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setConfirmationEmailSent(false);
        order.setCreatedAt(Instant.now());
        order.setCreatedBy("test");
        return order;
    }
}
