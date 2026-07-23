package com.duytoan.imajicoffee.imaji_coffee_be.services.email.template;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OrderConfirmationEmailModelFactoryTest {

    @Test
    void build_formatsTotalsAndCustomerData() throws Exception {
        OrderConfirmationEmailModelFactory factory = new OrderConfirmationEmailModelFactory(new ObjectMapper());
        ReflectionTestUtils.setField(factory, "frontendBaseUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(factory, "logoUrl", "http://localhost:5173/logo/logo.png");
        ReflectionTestUtils.setField(factory, "contactEmail", "support@imajicoffee.com");
        ReflectionTestUtils.setField(factory, "contactPhone", "+1 800 123 456");
        ReflectionTestUtils.setField(factory, "contactAddress", "Seattle");
        ReflectionTestUtils.setField(factory, "privacyPolicyUrl", "http://localhost:5173/privacy-policy");
        ReflectionTestUtils.setField(factory, "termsUrl", "http://localhost:5173/terms");

        Order order = new Order();
        order.setOrderId(25L);
        order.setStatus(OrderStatus.PAID);
        order.setPaymentMethod("card");
        order.setCurrency("USD");
        order.setCreatedAt(Instant.parse("2026-01-01T10:15:30Z"));
        order.setShippingMethod("Standard 3-5 days");
        order.setShippingAddress(new ObjectMapper().writeValueAsString(new AddressDto(
                null, 1L, "Jane Doe", "US", "WA", "Seattle", "123 Bean St", "98101", null, "+1234567890", false
        )));

        OrderItem item = new OrderItem();
        item.setProductName("Iced Latte");
        item.setProductImg("/menu/coffee_baverage/Sections/Image-1.png");
        item.setPrice(new BigDecimal("12.50"));
        item.setQuantity(2);
        order.setOrderItems(Set.of(item));
        order.setDiscountAmount(new BigDecimal("1.50"));
        order.setTaxAmount(new BigDecimal("2.50"));
        order.setShippingAmount(new BigDecimal("5.00"));
        order.setTotalAmount(new BigDecimal("31.00"));

        OrderConfirmationEmailModel model = factory.build(order);

        assertThat(model.customerName()).isEqualTo("Jane Doe");
        assertThat(model.subtotal()).isEqualTo("$25.00");
        assertThat(model.discount()).isEqualTo("$1.50");
        assertThat(model.tax()).isEqualTo("$2.50");
        assertThat(model.shipping()).isEqualTo("$5.00");
        assertThat(model.total()).isEqualTo("$31.00");
        assertThat(model.viewOrderUrl()).isEqualTo("http://localhost:5173/completed-checkout/25");
        assertThat(model.items()).hasSize(1);
        assertThat(model.items().get(0).imageUrl())
                .isEqualTo("http://localhost:5173/menu/coffee_baverage/Sections/Image-1.png");
    }

    @Test
    void build_usesFallbacksWhenOptionalDataMissing() {
        OrderConfirmationEmailModelFactory factory = new OrderConfirmationEmailModelFactory(new ObjectMapper());
        ReflectionTestUtils.setField(factory, "frontendBaseUrl", "http://localhost:5173");
        ReflectionTestUtils.setField(factory, "logoUrl", "http://localhost:5173/logo/logo.png");
        ReflectionTestUtils.setField(factory, "contactEmail", "support@imajicoffee.com");
        ReflectionTestUtils.setField(factory, "contactPhone", "+1 800 123 456");
        ReflectionTestUtils.setField(factory, "contactAddress", "Seattle");
        ReflectionTestUtils.setField(factory, "privacyPolicyUrl", "http://localhost:5173/privacy-policy");
        ReflectionTestUtils.setField(factory, "termsUrl", "http://localhost:5173/terms");

        Order order = new Order();
        order.setOrderId(99L);
        order.setStatus(OrderStatus.PAID);
        order.setPaymentMethod("paypal");
        order.setCurrency("USD");
        order.setOrderItems(Set.of());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setShippingAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);

        OrderConfirmationEmailModel model = factory.build(order);

        assertThat(model.customerName()).isEqualTo("Coffee Lover");
        assertThat(model.shippingAddress()).isEqualTo("Address unavailable");
        assertThat(model.estimatedDelivery()).isEqualTo("We will update your delivery timing shortly.");
    }
}
