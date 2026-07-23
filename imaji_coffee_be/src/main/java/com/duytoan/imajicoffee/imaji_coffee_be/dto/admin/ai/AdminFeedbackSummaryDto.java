package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

import java.util.List;

public record AdminFeedbackSummaryDto(
        int analyzedMessages,
        String sentiment,
        List<String> recurringIssues,
        String note
) {
}
