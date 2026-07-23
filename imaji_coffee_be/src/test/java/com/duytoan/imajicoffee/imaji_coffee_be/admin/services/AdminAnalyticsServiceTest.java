package com.duytoan.imajicoffee.imaji_coffee_be.admin.services;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.AdminDashboardSummaryDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.chat.ChatMessageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderItemRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai.AdminAnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAnalyticsServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private PromoUsageRepository promoUsageRepository;
    @Mock private ChatMessageRepository chatMessageRepository;

    @Test
    void buildSummary_returnsDeterministicMetricsAndStats() {
        AdminAnalyticsService service = new AdminAnalyticsService(
                orderRepository, orderItemRepository, productRepository, promoUsageRepository, chatMessageRepository
        );

        when(orderRepository.countByCreatedAtBetween(any(), any())).thenReturn(2L, 10L);
        when(orderRepository.countByStatusAndCreatedAtBetween(eq(OrderStatus.REFUNDED), any(), any())).thenReturn(1L);
        when(orderRepository.sumTotalAmountByStatusesAndCreatedAtBetween(anyList(), any(), any()))
                .thenReturn(new BigDecimal("120.00"), new BigDecimal("40.00"), new BigDecimal("50.00"));
        when(orderItemRepository.findTopSellingProducts(any(), any()))
                .thenReturn(new ArrayList<>(List.<Object[]>of(new Object[]{1L, "Iced Latte", "coffee_baverage", 20L})));
        when(productRepository.findByIsAvailableAtWebTrueAndQuantityLessThanEqual(anyInt()))
                .thenReturn(List.of(lowStockProduct()));
        when(promoUsageRepository.countPromoUsageByCode(any(), any()))
                .thenReturn(new ArrayList<>(List.<Object[]>of(new Object[]{"SUMMER10", 5L})));
        when(orderRepository.countByUserForStatusAndRange(eq(OrderStatus.PAYMENT_FAILED), any(), any()))
                .thenReturn(List.of());
        when(promoUsageRepository.countPromoUsageByUser(any(), any()))
                .thenReturn(List.of());
        when(orderRepository.findByCreatedAtBetween(any(), any()))
                .thenReturn(List.of(order("12.00"), order("14.00")));
        when(chatMessageRepository.findRecentBySenderType(any(), any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        AdminDashboardSummaryDto summary = service.buildSummary();

        assertThat(summary.metrics()).isNotEmpty();
        assertThat(summary.metrics().stream().anyMatch(m -> m.label().equals("Orders today") && m.value().equals("2"))).isTrue();
        assertThat(summary.popularProducts()).hasSize(1);
        assertThat(summary.popularProducts().get(0).productName()).isEqualTo("Iced Latte");
        assertThat(summary.topPromoCodes()).hasSize(1);
        assertThat(summary.topPromoCodes().get(0).code()).isEqualTo("SUMMER10");
    }

    private Product lowStockProduct() {
        Product p = new Product();
        p.setProductId(10L);
        p.setName("Cold Brew");
        p.setCategory("coffee_baverage");
        p.setQuantity(3);
        p.setPrice(new BigDecimal("4.00"));
        p.setIsAvailableAtWeb(true);
        return p;
    }

    private Order order(String total) {
        Order o = new Order();
        o.setTotalAmount(new BigDecimal(total));
        o.setCreatedAt(Instant.now());
        return o;
    }
}
