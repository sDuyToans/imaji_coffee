package com.duytoan.imajicoffee.imaji_coffee_be.dto.faq;

public record FAQCreateRequest(
        String question,
        String answer,
        String category,
        Boolean isActive
) {
}
