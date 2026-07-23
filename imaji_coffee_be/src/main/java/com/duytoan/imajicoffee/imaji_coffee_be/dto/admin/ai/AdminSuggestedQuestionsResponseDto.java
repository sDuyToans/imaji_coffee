package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

import java.util.List;

public record AdminSuggestedQuestionsResponseDto(
        List<String> questions
) {
}
