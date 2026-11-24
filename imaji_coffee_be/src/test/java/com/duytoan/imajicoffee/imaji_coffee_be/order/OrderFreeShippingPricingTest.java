package com.duytoan.imajicoffee.imaji_coffee_be.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderItemDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.payment.PaymentIntentResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.payment.PaymentWebhookEventRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.impl.AddressServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.email.IMailService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.OrderItemServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.OrderServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.payment.IPaymentService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFreeShippingPricingTest {
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemServiceImpl orderItemService;
    @Mock private IPaymentService paymentService;
    @Mock private ShipRepository shipRepository;
    @Mock private UserRepository userRepository;
    @Mock private AddressServiceImpl addressService;
    @Mock private IMailService mailService;
    @Mock private PromoRepository promoRepository;
    @Mock private PromoUsageRepository promoUsageRepository;
    @Mock private PaymentWebhookEventRepository webhookRepository;
    @Mock private ProductRepository productRepository;

    private OrderServiceImpl service;
    private Ship ship;
    private Promo promo;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderRepository, new ObjectMapper(), orderItemService,
                paymentService, shipRepository, userRepository, addressService, mailService,
                promoRepository, promoUsageRepository, webhookRepository, productRepository,
                new PromoPricingService(promoUsageRepository));
        Product product = new Product();
        product.setProductId(101L);
        product.setQuantity(10);
        product.setIsAvailableAtWeb(true);
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        User user = new User();
        user.setUserId(10L);
        user.setUsername("tester");
        user.setEmail("tester@example.invalid");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        ship = new Ship();
        ship.setMethodName("Standard");
        ship.setPrice(new BigDecimal("5.00"));
        when(shipRepository.findById(1L)).thenReturn(Optional.of(ship));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            savedOrder = invocation.getArgument(0);
            savedOrder.setOrderId(42L);
            return savedOrder;
        });
        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setProductCategory("coffee_baverage");
        item.setPrice(new BigDecimal("10.00"));
        item.setQuantity(1);
        when(orderItemService.saveOrderItems(any(Order.class), anyList())).thenReturn(List.of(item));
        promo = new Promo();
        promo.setPromoId(1L);
        promo.setCode("FREE");
        promo.setIsActive(true);
        promo.setDiscountType("free_shipping");
        promo.setDiscountValue(BigDecimal.ZERO);
    }

    @ParameterizedTest
    @CsvSource({
            "free_shipping, 0, 5.00, 5.00, 11.00, 1100",
            "free_shipping, 0, 0.00, 0.00, 11.00, 1100",
            "free_shipping, 0, 20.00, 20.00, 11.00, 1100",
            "free_shipping, 0, 2.35, 2.35, 11.00, 1100",
            "percentage, 10, 5.00, 1.00, 15.00, 1500",
            "fixed, 3, 5.00, 3.00, 13.00, 1300",
            "none, 0, 5.00, 0.00, 16.00, 1600"
    })
    void createOrder_persistsAndChargesTheSameTotal(String type, String value, String shipping,
                                                   String discount, String total, long cents) throws Exception {
        ship.setPrice(new BigDecimal(shipping));
        String code = "none".equals(type) ? null : "FREE";
        if (code != null) {
            promo.setDiscountType(type);
            promo.setDiscountValue(new BigDecimal(value));
            stubPromo(true);
        }
        when(paymentService.createPaymentIntent(any())).thenReturn(new PaymentIntentResponseDto("pi_test", "cs_test"));

        OrderResponseDto response = service.createOrder(request("card", code), 10L);

        assertBreakdown(response, shipping, discount, total);
        ArgumentCaptor<PaymentIntentRequestDto> intent = ArgumentCaptor.forClass(PaymentIntentRequestDto.class);
        verify(paymentService).createPaymentIntent(intent.capture());
        assertThat(intent.getValue().amount()).isEqualTo(cents);
        assertThat(intent.getValue().currency()).isEqualTo("usd");
        assertThat(intent.getValue().orderId()).isEqualTo(42L);
        assertThat(intent.getValue().idempotencyKey()).isEqualTo("checkout-test");
        assertThat(response.clientSecret()).isEqualTo("cs_test");
        verify(mailService, never()).sendOrderInfoToEmail(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"cod", "paypal"})
    void createOrder_nonCardUsesTheSameSingleShippingWaiver(String method) throws Exception {
        stubPromo(true);

        OrderResponseDto response = "paypal".equals(method)
                ? service.createOrderForPaypal(request(method, "FREE"), 10L)
                : service.createOrder(request(method, "FREE"), 10L);

        assertBreakdown(response, "5.00", "5.00", "11.00");
        verifyNoInteractions(paymentService);
        assertThat(response.clientSecret()).isNull();
        if ("cod".equals(method)) {
            verify(mailService).sendOrderInfoToEmail(savedOrder);
        } else {
            verifyNoInteractions(mailService);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"inactive", "expired", "ineligible", "exhausted"})
    void createOrder_rejectsUnusableFreeShippingBeforeCreatingPayment(String reason) {
        switch (reason) {
            case "inactive" -> promo.setIsActive(false);
            case "expired" -> promo.setEndAt(Instant.now().minusSeconds(60));
            case "ineligible" -> promo.setRestrictedUserId(99L);
            case "exhausted" -> {
                promo.setMaxTotalUses(1);
                promo.setUsageCount(1);
            }
        }
        stubPromo(false);

        assertThatThrownBy(() -> service.createOrder(request("card", "FREE"), 10L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(paymentService, never()).createPaymentIntent(any());
        verifyNoInteractions(promoUsageRepository, mailService, addressService);
    }

    private void stubPromo(boolean consumed) {
        when(promoRepository.findByCodeForUpdate("FREE")).thenReturn(Optional.of(promo));
        if (consumed) {
            when(promoRepository.findByPromoIdForUpdate(1L)).thenReturn(Optional.of(promo));
        }
    }

    private OrderRequestDto request(String method, String code) {
        AddressDto address = new AddressDto(null, 10L, "Test User", "US", "CA", "SF",
                "Market Street", "94105", "Apt 1", "+14155550123", false);
        return new OrderRequestDto(method, 1L, address, code, "checkout-test",
                List.of(new OrderItemDto(101L, 1)));
    }

    private void assertBreakdown(OrderResponseDto response, String shipping, String discount, String total) {
        assertThat(response.totalAmount()).isEqualByComparingTo(total);
        assertThat(response.subtotalAmount()).isEqualByComparingTo("10.00");
        assertThat(response.taxAmount()).isEqualByComparingTo("1.00");
        assertThat(response.shippingAmount()).isEqualByComparingTo(shipping);
        assertThat(response.discountAmount()).isEqualByComparingTo(discount);
        assertThat(response.totalAmount().scale()).isEqualTo(2);
        assertThat(response.totalAmount()).isEqualByComparingTo(response.subtotalAmount()
                .add(response.taxAmount()).add(response.shippingAmount()).subtract(response.discountAmount()));
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(total);
        assertThat(savedOrder.getShippingAmount()).isEqualByComparingTo(shipping);
        assertThat(savedOrder.getDiscountAmount()).isEqualByComparingTo(discount);
        assertThat(savedOrder.getCurrency()).isEqualTo("USD");
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.paymentStatus()).isEqualTo("UNPAID");
    }
}
