package com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.*;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatMessage;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderType;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.chat.ChatMessageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderItemRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final List<OrderStatus> SUCCESS_STATUSES = List.of(
            OrderStatus.PAID,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
    );

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final PromoUsageRepository promoUsageRepository;
    private final ChatMessageRepository chatMessageRepository;

    public AdminDashboardSummaryDto buildSummary() {
        Instant now = Instant.now();
        Instant monthStart = LocalDate.now(ZoneOffset.UTC).withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant todayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant weekStart = now.minusSeconds(7L * 24 * 3600);
        Instant prevWeekStart = now.minusSeconds(14L * 24 * 3600);
        Instant monthAgo = now.minusSeconds(30L * 24 * 3600);

        long ordersToday = orderRepository.countByCreatedAtBetween(todayStart, now);
        long ordersMonth = orderRepository.countByCreatedAtBetween(monthStart, now);
        long refundsMonth = orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.REFUNDED, monthStart, now);
        BigDecimal revenueMonth = safe(orderRepository.sumTotalAmountByStatusesAndCreatedAtBetween(SUCCESS_STATUSES, monthStart, now));
        BigDecimal revenueWeek = safe(orderRepository.sumTotalAmountByStatusesAndCreatedAtBetween(SUCCESS_STATUSES, weekStart, now));
        BigDecimal revenuePrevWeek = safe(orderRepository.sumTotalAmountByStatusesAndCreatedAtBetween(SUCCESS_STATUSES, prevWeekStart, weekStart));

        String weeklyTrend = revenuePrevWeek.compareTo(BigDecimal.ZERO) <= 0
                ? "stable"
                : revenueWeek.compareTo(revenuePrevWeek) >= 0 ? "up" : "down";

        List<AdminMetricDto> metrics = List.of(
                new AdminMetricDto("Orders today", String.valueOf(ordersToday), "daily"),
                new AdminMetricDto("Orders this month", String.valueOf(ordersMonth), "monthly"),
                new AdminMetricDto("Revenue this month", money(revenueMonth), "monthly"),
                new AdminMetricDto("Refunds this month", String.valueOf(refundsMonth), "monthly"),
                new AdminMetricDto("Weekly revenue trend", money(revenueWeek), weeklyTrend)
        );

        List<AdminProductStatDto> popularProducts = orderItemRepository.findTopSellingProducts(monthStart, now).stream()
                .limit(6)
                .map(row -> new AdminProductStatDto(
                        asLong(row[0]),
                        asString(row[1]),
                        asString(row[2]),
                        asLong(row[3])
                ))
                .toList();

        List<AdminLowStockDto> lowStockProducts = productRepository.findByIsAvailableAtWebTrueAndQuantityLessThanEqual(LOW_STOCK_THRESHOLD).stream()
                .sorted(Comparator.comparing(Product::getQuantity))
                .limit(8)
                .map(p -> new AdminLowStockDto(
                        p.getProductId(),
                        p.getName(),
                        p.getCategory(),
                        p.getQuantity(),
                        p.getPrice()
                ))
                .toList();

        List<AdminPromoUsageDto> topPromoCodes = promoUsageRepository.countPromoUsageByCode(monthAgo, now).stream()
                .limit(6)
                .map(row -> new AdminPromoUsageDto(asString(row[0]), asLong(row[1])))
                .toList();

        List<AdminAlertDto> riskAlerts = buildRiskAlerts(now, revenueWeek, revenuePrevWeek);
        List<AdminRecommendationDto> inventoryRecommendations = buildInventoryRecommendations(popularProducts, lowStockProducts);
        AdminFeedbackSummaryDto feedbackSummary = buildFeedbackSummary(now);

        return new AdminDashboardSummaryDto(
                "AI-generated insights are recommendations, not guaranteed facts.",
                now,
                metrics,
                popularProducts,
                lowStockProducts,
                topPromoCodes,
                riskAlerts,
                inventoryRecommendations,
                feedbackSummary
        );
    }

    private List<AdminAlertDto> buildRiskAlerts(Instant now, BigDecimal revenueWeek, BigDecimal revenuePrevWeek) {
        List<AdminAlertDto> alerts = new ArrayList<>();

        if (revenuePrevWeek.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal dropRatio = revenuePrevWeek.subtract(revenueWeek)
                    .divide(revenuePrevWeek, 4, RoundingMode.HALF_UP);
            if (dropRatio.compareTo(new BigDecimal("0.20")) > 0) {
                alerts.add(new AdminAlertDto(
                        "HIGH",
                        "Sales slowdown",
                        "Weekly revenue dropped by more than 20% compared to the previous week."
                ));
            }
        }

        Instant recent = now.minusSeconds(24L * 3600);
        List<Object[]> failedPaymentsByUser = orderRepository.countByUserForStatusAndRange(OrderStatus.PAYMENT_FAILED, recent, now);
        failedPaymentsByUser.stream()
                .filter(row -> asLong(row[1]) >= 3)
                .forEach(row -> alerts.add(new AdminAlertDto(
                        "MEDIUM",
                        "Repeated payment failures",
                        "User " + asLong(row[0]) + " has " + asLong(row[1]) + " payment failures in the last 24 hours."
                )));

        List<Object[]> promoUsageByUser = promoUsageRepository.countPromoUsageByUser(now.minusSeconds(7L * 24 * 3600), now);
        promoUsageByUser.stream()
                .filter(row -> asLong(row[1]) >= 5)
                .forEach(row -> alerts.add(new AdminAlertDto(
                        "MEDIUM",
                        "High promo usage",
                        "User " + asLong(row[0]) + " used promo codes " + asLong(row[1]) + " times this week."
                )));

        List<Order> ordersLastWeek = orderRepository.findByCreatedAtBetween(now.minusSeconds(7L * 24 * 3600), now);
        if (!ordersLastWeek.isEmpty()) {
            BigDecimal avg = ordersLastWeek.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(ordersLastWeek.size()), 2, RoundingMode.HALF_UP);
            BigDecimal threshold = avg.multiply(new BigDecimal("2.5"));
            long unusual = ordersLastWeek.stream()
                    .filter(o -> o.getTotalAmount() != null && o.getTotalAmount().compareTo(threshold) > 0)
                    .count();
            if (unusual > 0) {
                alerts.add(new AdminAlertDto(
                        "LOW",
                        "Unusual high-value orders",
                        unusual + " orders exceeded 2.5x the weekly average order value."
                ));
            }
        }

        if (alerts.isEmpty()) {
            alerts.add(new AdminAlertDto("LOW", "No major risk detected", "No unusual payment, promo, or revenue anomalies were detected."));
        }
        return alerts;
    }

    private List<AdminRecommendationDto> buildInventoryRecommendations(
            List<AdminProductStatDto> popularProducts,
            List<AdminLowStockDto> lowStockProducts
    ) {
        Map<Long, Long> soldByProduct = popularProducts.stream()
                .collect(Collectors.toMap(AdminProductStatDto::productId, AdminProductStatDto::quantity, (a, b) -> a));
        return lowStockProducts.stream()
                .limit(5)
                .map(low -> {
                    long monthlySales = soldByProduct.getOrDefault(low.productId(), 0L);
                    long estWeeklySales = Math.max(1, Math.round(monthlySales / 4.0));
                    int suggested = Math.max(20, (int) estWeeklySales * 2);
                    return new AdminRecommendationDto(
                            "Restock " + low.productName(),
                            "Current stock is " + low.currentStock() + ". Recommended restock: +" + suggested + " units based on recent sales.",
                            monthlySales > 0 ? "MEDIUM" : "LOW"
                    );
                })
                .toList();
    }

    private AdminFeedbackSummaryDto buildFeedbackSummary(Instant now) {
        List<ChatMessage> recent = chatMessageRepository.findRecentBySenderType(
                SenderType.USER,
                now.minusSeconds(30L * 24 * 3600),
                now,
                PageRequest.of(0, 200)
        );

        if (recent.isEmpty()) {
            return new AdminFeedbackSummaryDto(
                    0,
                    "unknown",
                    List.of(),
                    "No customer feedback messages were available for this period."
            );
        }

        int negative = 0;
        int positive = 0;
        Map<String, Integer> issueHits = new HashMap<>();
        List<String> issueKeywords = List.of("late", "delay", "refund", "cold", "wrong", "missing", "payment", "coupon");
        for (ChatMessage message : recent) {
            String content = Optional.ofNullable(message.getContent()).orElse("").toLowerCase(Locale.ROOT);
            if (content.contains("bad") || content.contains("problem") || content.contains("issue") || content.contains("late")) {
                negative++;
            }
            if (content.contains("great") || content.contains("thanks") || content.contains("good")) {
                positive++;
            }
            for (String keyword : issueKeywords) {
                if (content.contains(keyword)) {
                    issueHits.merge(keyword, 1, Integer::sum);
                }
            }
        }

        String sentiment = negative > positive ? "mostly negative" : positive > negative ? "mostly positive" : "mixed";
        List<String> recurring = issueHits.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(4)
                .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                .toList();

        return new AdminFeedbackSummaryDto(
                recent.size(),
                sentiment,
                recurring,
                "Feedback summary is inferred from support chat text and may be incomplete."
        );
    }

    private String money(BigDecimal value) {
        return "$" + safe(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Long asLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private String asString(Object value) {
        return value == null ? "" : value.toString();
    }
}
