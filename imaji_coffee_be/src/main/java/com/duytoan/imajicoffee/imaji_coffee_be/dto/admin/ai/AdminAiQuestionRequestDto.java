package com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminAiQuestionRequestDto(
        @NotBlank(message = "Question is required")
        @Size(max = 500, message = "Question must be 500 characters or fewer")
        String question
) {
}
