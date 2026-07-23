package com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai.provider;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.AdminDashboardSummaryDto;

import java.util.List;

public interface AdminAiProvider {
    AdminAiAnswer answer(String sanitizedQuestion, AdminDashboardSummaryDto summary);

    record AdminAiAnswer(
            String answer,
            List<String> evidence,
            List<String> suggestedQuestions
    ) {
    }
}
