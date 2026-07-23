package com.duytoan.imajicoffee.imaji_coffee_be.services.email.template;

import java.util.List;

public record OrderConfirmationEmailModel(
        String customerName,
        String orderNumber,
        String orderDate,
        String paymentStatus,
        String estimatedDelivery,
        String shippingAddress,
        String paymentMethod,
        String subtotal,
        String discount,
        String tax,
        String shipping,
        String total,
        String currency,
        String viewOrderUrl,
        String continueShoppingUrl,
        String contactSupportUrl,
        String privacyPolicyUrl,
        String termsUrl,
        String preferencesUrl,
        String brandLogoUrl,
        String contactEmail,
        String contactPhone,
        String contactAddress,
        String currentYear,
        List<OrderConfirmationEmailItemModel> items
) {
}
