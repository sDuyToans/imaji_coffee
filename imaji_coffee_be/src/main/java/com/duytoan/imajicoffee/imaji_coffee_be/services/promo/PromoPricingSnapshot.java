package com.duytoan.imajicoffee.imaji_coffee_be.services.promo;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;

import java.math.BigDecimal;

public record PromoPricingSnapshot(
        boolean accepted,
        String message,
        String eligibilityHint,
        BigDecimal discountAmount,
        BigDecimal shippingAmount,
        Promo promo,
        BigDecimal eligibleSubtotal
) {
}
