package com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai.provider;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class HeuristicAdminAiProvider implements AdminAiProvider {

    @Override
    public AdminAiAnswer answer(String sanitizedQuestion, AdminDashboardSummaryDto summary) {
        String question = sanitizedQuestion.toLowerCase(Locale.ROOT);
        String answer;
        List<String> evidence = new ArrayList<>();

        if (question.contains("best-selling") || question.contains("best selling") || question.contains("popular")) {
            String top = summary.popularProducts().stream()
                    .limit(3)
                    .map(p -> p.productName() + " (" + p.quantity() + " sold)")
                    .collect(Collectors.joining(", "));
            answer = top.isBlank()
                    ? "No paid-order sales data was found for this period."
                    : "Top-selling products this period are: " + top + ".";
            evidence.add("Popular products are computed from paid/fulfilled order items.");
        } else if (question.contains("run out") || question.contains("low stock") || question.contains("inventory")) {
            String lowStock = summary.lowStockProducts().stream()
                    .limit(5)
                    .map(p -> p.productName() + " (stock " + p.currentStock() + ")")
                    .collect(Collectors.joining(", "));
            answer = lowStock.isBlank()
                    ? "No low-stock products are currently flagged."
                    : "Products at risk of stockout soon: " + lowStock + ".";
            evidence.add("Low stock threshold is based on current stock <= configured alert threshold.");
        } else if (question.contains("sales decrease") || question.contains("decrease this week")) {
            String trend = summary.metrics().stream()
                    .filter(m -> "Weekly revenue trend".equalsIgnoreCase(m.label()))
                    .map(AdminMetricDto::trend)
                    .findFirst()
                    .orElse("stable");
            answer = "Weekly revenue appears " + trend + ". Common drivers include lower paid-order volume, promo mix changes, and stock constraints.";
            evidence.add("Trend compares this 7-day paid revenue with the previous 7-day window.");
        } else if (question.contains("promo code") || question.contains("promo")) {
            String promo = summary.topPromoCodes().stream()
                    .limit(5)
                    .map(p -> p.code() + " (" + p.usageCount() + " uses)")
                    .collect(Collectors.joining(", "));
            answer = promo.isBlank()
                    ? "No promo usage was recorded in the selected time window."
                    : "Most-used promo codes are: " + promo + ".";
            evidence.add("Promo usage is counted from promo_usage records, not client-submitted totals.");
        } else {
            answer = "I can help with sales trends, low-stock risk, promo usage, refund patterns, and unusual activity. Try one of the suggested questions.";
            evidence.add("Current response is based on aggregated backend analytics only.");
        }

        List<String> suggestions = List.of(
                "What were our best-selling products this month?",
                "Which products may run out soon?",
                "Why did sales decrease this week?",
                "Which promo codes were used most often?"
        );

        return new AdminAiAnswer(
                answer + " This is an AI recommendation and may be incomplete.",
                evidence,
                suggestions
        );
    }
}
