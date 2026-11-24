package com.duytoan.imajicoffee.imaji_coffee_be.services.promo;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;

import java.math.BigDecimal;

/**
 * Promotion amounts for totals calculated as subtotal + tax + shippingAmount - discountAmount.
 *
 * @param discountAmount benefit subtracted once from the total, including a shipping waiver
 * @param shippingAmount shipping charge before the promotion
 */
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
