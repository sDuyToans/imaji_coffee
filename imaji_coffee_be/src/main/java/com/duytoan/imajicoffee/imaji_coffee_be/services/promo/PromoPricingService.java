package com.duytoan.imajicoffee.imaji_coffee_be.services.promo;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromoPricingService {

    private final PromoUsageRepository promoUsageRepository;

    public PromoPricingSnapshot evaluate(
            Promo promo,
            Long userId,
            List<OrderItem> items,
            BigDecimal subtotal,
            BigDecimal shippingAmount,
            boolean checkUsage
    ) {
        if (promo == null) {
            return rejected("Promo code is invalid", "invalid", shippingAmount);
        }

        if (!Boolean.TRUE.equals(promo.getIsActive()) || !"ACTIVE".equalsIgnoreCase(promo.getStatus())) {
            return rejected("Promo code is inactive", "inactive", shippingAmount);
        }

        Instant now = Instant.now();
        if (promo.getStartAt() != null && promo.getStartAt().isAfter(now)) {
            return rejected("Promo code is not available yet", "upcoming", shippingAmount);
        }
        if (promo.getEndAt() != null && promo.getEndAt().isBefore(now)) {
            return rejected("Promo code has expired", "expired", shippingAmount);
        }

        if (promo.getRestrictedUserId() != null && !promo.getRestrictedUserId().equals(userId)) {
            return rejected("Promo code is not eligible for this account", "restricted-user", shippingAmount);
        }

        BigDecimal minAmount = promo.getMinimumOrderAmount() == null
                ? BigDecimal.ZERO
                : promo.getMinimumOrderAmount().setScale(2, RoundingMode.HALF_UP);
        if (subtotal.compareTo(minAmount) < 0) {
            return rejected("Minimum order amount is not reached for this promo", "min-order", shippingAmount);
        }

        if (checkUsage && promo.getMaxTotalUses() != null && promo.getUsageCount() != null
                && promo.getUsageCount() >= promo.getMaxTotalUses()) {
            return rejected("Promo code usage limit has been reached", "usage-limit", shippingAmount);
        }

        if (checkUsage && promo.getMaxUsesPerUser() != null && promo.getMaxUsesPerUser() > 0) {
            long usedByUser = promoUsageRepository.countByPromo_PromoIdAndUserId(promo.getPromoId(), userId);
            if (usedByUser >= promo.getMaxUsesPerUser()) {
                return rejected("You have already used this promo code", "already-used", shippingAmount);
            }
        }

        Set<Long> eligibleProductIds = promo.getPromoProducts() == null ? Set.of() : promo.getPromoProducts().stream()
                .map(pp -> pp.getProduct().getProductId())
                .collect(Collectors.toSet());

        String eligibleCategory = normalize(promo.getEligibleCategory());
        boolean hasProductRestriction = !eligibleProductIds.isEmpty();
        boolean hasCategoryRestriction = eligibleCategory != null;

        BigDecimal eligibleSubtotal = items.stream()
                .filter(item -> isEligibleItem(item, eligibleProductIds, eligibleCategory, hasProductRestriction, hasCategoryRestriction))
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if ((hasProductRestriction || hasCategoryRestriction) && eligibleSubtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return rejected("Promo code does not apply to the selected products", "product-eligibility", shippingAmount);
        }

        BigDecimal effectiveSubtotal = (hasProductRestriction || hasCategoryRestriction) ? eligibleSubtotal : subtotal;
        BigDecimal safeShipping = shippingAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        switch (normalizeDiscountType(promo.getDiscountType())) {
            case "percentage" -> {
                if (promo.getDiscountValue() == null || promo.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                    return rejected("Promo code configuration is invalid", "invalid-config", shippingAmount);
                }
                discount = effectiveSubtotal
                        .multiply(promo.getDiscountValue().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            case "fixed" -> {
                if (promo.getDiscountValue() == null || promo.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                    return rejected("Promo code configuration is invalid", "invalid-config", shippingAmount);
                }
                discount = promo.getDiscountValue().min(effectiveSubtotal).setScale(2, RoundingMode.HALF_UP);
            }
            case "free_shipping" -> {
                discount = safeShipping;
                safeShipping = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }
            default -> {
                return rejected("Promo code is not supported", "invalid-type", shippingAmount);
            }
        }

        if (discount.compareTo(effectiveSubtotal) > 0 && !"free_shipping".equalsIgnoreCase(normalizeDiscountType(promo.getDiscountType()))) {
            discount = effectiveSubtotal;
        }
        return new PromoPricingSnapshot(
                true,
                "Promo code applied successfully",
                "eligible",
                discount.setScale(2, RoundingMode.HALF_UP),
                safeShipping,
                promo,
                effectiveSubtotal
        );
    }

    private PromoPricingSnapshot rejected(String message, String eligibilityHint, BigDecimal shippingAmount) {
        return new PromoPricingSnapshot(
                false,
                message,
                eligibilityHint,
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                shippingAmount.setScale(2, RoundingMode.HALF_UP),
                null,
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private boolean isEligibleItem(
            OrderItem item,
            Set<Long> eligibleProductIds,
            String eligibleCategory,
            boolean hasProductRestriction,
            boolean hasCategoryRestriction
    ) {
        boolean productMatch = !hasProductRestriction || eligibleProductIds.contains(item.getProductId());
        boolean categoryMatch = !hasCategoryRestriction || normalize(item.getProductCategory()) != null
                && normalize(item.getProductCategory()).equals(eligibleCategory);
        return productMatch && categoryMatch;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeDiscountType(String value) {
        String normalized = normalize(value);
        return normalized == null ? "" : normalized;
    }
}
