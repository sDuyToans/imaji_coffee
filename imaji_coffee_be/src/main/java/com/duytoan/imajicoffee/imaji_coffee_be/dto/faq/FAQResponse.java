package com.duytoan.imajicoffee.imaji_coffee_be.dto.faq;

import java.time.Instant;

public record FAQResponse(
        Long id,
        String question,
        String answer,
        String category,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt) {
}
