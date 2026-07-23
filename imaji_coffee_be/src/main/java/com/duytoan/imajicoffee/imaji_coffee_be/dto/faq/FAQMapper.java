package com.duytoan.imajicoffee.imaji_coffee_be.dto.faq;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;

public final class FAQMapper {
    private FAQMapper(){}
    public static FAQResponse toDto(FAQ f){
        return new FAQResponse(
                f.getId(),
                f.getQuestion(),
                f.getAnswer(),
                f.getCategory(),
                f.getIsActive(),
                f.getCreatedAt() != null ? f.getCreatedAt() : null,
                f.getUpdatedAt() != null ? f.getUpdatedAt() : null
        );
    }
}
