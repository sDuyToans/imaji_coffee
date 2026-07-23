package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

import java.time.Instant;
import java.util.List;

public record AdminAiQuestionResponseDto(
        String answer,
        String recommendationLabel,
        List<String> evidence,
        List<String> suggestedQuestions,
        Instant generatedAt
) {
}
