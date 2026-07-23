package com.duytoan.imajicoffee.imaji_coffee_be.dto.faq;

public record FAQUpdateRequest(
        String question,
        String answer,
        String category,
        Boolean isActive
) {
}
