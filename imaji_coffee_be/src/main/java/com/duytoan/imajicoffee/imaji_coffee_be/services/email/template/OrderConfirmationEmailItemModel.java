package com.duytoan.imajicoffee.imaji_coffee_be.services.email.template;

public record OrderConfirmationEmailItemModel(
        String name,
        Integer quantity,
        String unitPrice,
        String lineTotal,
        String imageUrl
) {
}
