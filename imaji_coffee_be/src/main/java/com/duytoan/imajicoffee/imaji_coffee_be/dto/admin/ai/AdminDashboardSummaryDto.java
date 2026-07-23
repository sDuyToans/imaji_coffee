package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

import java.time.Instant;
import java.util.List;

public record AdminDashboardSummaryDto(
        String disclaimer,
        Instant generatedAt,
        List<AdminMetricDto> metrics,
        List<AdminProductStatDto> popularProducts,
        List<AdminLowStockDto> lowStockProducts,
        List<AdminPromoUsageDto> topPromoCodes,
        List<AdminAlertDto> riskAlerts,
        List<AdminRecommendationDto> inventoryRecommendations,
        AdminFeedbackSummaryDto feedbackSummary
) {
}
