package com.duytoan.imajicoffee.imaji_coffee_be.services.email.template;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class OrderConfirmationEmailModelFactory {

    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a z");
    private static final String DEFAULT_CUSTOMER_NAME = "Coffee Lover";
    private static final String DEFAULT_ESTIMATED_DELIVERY = "We will update your delivery timing shortly.";
    private static final String DEFAULT_ADDRESS = "Address unavailable";

    private final ObjectMapper objectMapper;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.email.logo-url:http://localhost:5173/logo/logo.png}")
    private String logoUrl;

    @Value("${app.email.contact-email:support@imajicoffee.com}")
    private String contactEmail;

    @Value("${app.email.contact-phone:+1 (800) 426-3333}")
    private String contactPhone;

    @Value("${app.email.contact-address:123 Roast Street, Seattle, WA}")
    private String contactAddress;

    @Value("${app.email.privacy-url:http://localhost:5173/privacy-policy}")
    private String privacyPolicyUrl;

    @Value("${app.email.terms-url:http://localhost:5173/terms-and-conditions}")
    private String termsUrl;

    public OrderConfirmationEmailModel build(Order order) {
        AddressDto address = parseAddress(order.getShippingAddress());
        String customerName = fallback(address == null ? null : address.name(), DEFAULT_CUSTOMER_NAME);
        String shippingAddress = formatAddress(address);
        String currencyCode = fallback(order.getCurrency(), "USD");

        BigDecimal subtotalAmount = subtotal(order);
        BigDecimal discountAmount = amount(order.getDiscountAmount());
        BigDecimal taxAmount = amount(order.getTaxAmount());
        BigDecimal shippingAmount = amount(order.getShippingAmount());
        BigDecimal totalAmount = amount(order.getTotalAmount());

        List<OrderConfirmationEmailItemModel> items = order.getOrderItems().stream()
                .map(item -> toItemModel(item, currencyCode))
                .toList();

        String baseUrl = stripTrailingSlash(fallback(frontendBaseUrl, "http://localhost:5173"));
        String resolvedLogoUrl = resolveAssetUrl(baseUrl, fallback(logoUrl, baseUrl + "/logo/logo.png"), baseUrl + "/logo/logo.png");
        return new OrderConfirmationEmailModel(
                customerName,
                "#" + order.getOrderId(),
                formatOrderDate(order.getCreatedAt()),
                formatStatus(order),
                fallback(order.getShippingMethod(), DEFAULT_ESTIMATED_DELIVERY),
                shippingAddress,
                formatPaymentMethod(order.getPaymentMethod()),
                formatCurrency(subtotalAmount, currencyCode),
                formatCurrency(discountAmount, currencyCode),
                formatCurrency(taxAmount, currencyCode),
                formatCurrency(shippingAmount, currencyCode),
                formatCurrency(totalAmount, currencyCode),
                currencyCode,
                baseUrl + "/completed-checkout/" + order.getOrderId(),
                baseUrl + "/menu",
                baseUrl + "/chat",
                fallback(privacyPolicyUrl, baseUrl + "/privacy-policy"),
                fallback(termsUrl, baseUrl + "/terms-and-conditions"),
                baseUrl + "/account/setting",
                resolvedLogoUrl,
                fallback(contactEmail, "support@imajicoffee.com"),
                fallback(contactPhone, "+1 (800) 426-3333"),
                fallback(contactAddress, "123 Roast Street, Seattle, WA"),
                String.valueOf(Instant.now().atZone(ZoneId.systemDefault()).getYear()),
                items
        );
    }

    private OrderConfirmationEmailItemModel toItemModel(OrderItem item, String currencyCode) {
        BigDecimal unitPrice = amount(item.getPrice());
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
        String baseUrl = stripTrailingSlash(fallback(frontendBaseUrl, "http://localhost:5173"));
        String fallbackImage = resolveAssetUrl(baseUrl, fallback(logoUrl, baseUrl + "/logo/logo.png"), baseUrl + "/logo/logo.png");
        String productImage = resolveAssetUrl(baseUrl, item.getProductImg(), fallbackImage);
        return new OrderConfirmationEmailItemModel(
                fallback(item.getProductName(), "Product"),
                item.getQuantity() == null ? 0 : item.getQuantity(),
                formatCurrency(unitPrice, currencyCode),
                formatCurrency(lineTotal, currencyCode),
                productImage
        );
    }

    private String resolveAssetUrl(String baseUrl, String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String trimmed = raw.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("/")) {
            return baseUrl + trimmed;
        }
        return fallback;
    }

    private BigDecimal subtotal(Order order) {
        return order.getOrderItems().stream()
                .map(item -> amount(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal amount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatCurrency(BigDecimal value, String currencyCode) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        Currency currency;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (Exception ignored) {
            currency = Currency.getInstance("USD");
        }
        formatter.setCurrency(currency);
        return formatter.format(value);
    }

    private AddressDto parseAddress(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(rawAddress, AddressDto.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatAddress(AddressDto address) {
        if (address == null) {
            return DEFAULT_ADDRESS;
        }
        StringJoiner joiner = new StringJoiner(", ");
        addPart(joiner, address.street());
        addPart(joiner, address.apartment());
        addPart(joiner, address.city());
        addPart(joiner, address.province());
        addPart(joiner, address.postalCode());
        addPart(joiner, address.country());
        String result = joiner.toString();
        if (!result.isBlank() && address.phoneNumber() != null && !address.phoneNumber().isBlank()) {
            return result + " (" + address.phoneNumber() + ")";
        }
        return result.isBlank() ? DEFAULT_ADDRESS : result;
    }

    private String formatOrderDate(Instant createdAt) {
        Instant safeCreatedAt = createdAt == null ? Instant.now() : createdAt;
        return ORDER_DATE_FORMAT.format(safeCreatedAt.atZone(ZoneId.systemDefault()));
    }

    private String formatStatus(Order order) {
        if (order.getStatus() == null) {
            return "Pending";
        }
        String normalized = order.getStatus().name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String formatPaymentMethod(String paymentMethod) {
        String raw = fallback(paymentMethod, "card").toLowerCase(Locale.ROOT);
        return switch (raw) {
            case "paypal" -> "PayPal";
            case "card" -> "Card";
            default -> Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
        };
    }

    private String fallback(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private String stripTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private void addPart(StringJoiner joiner, String value) {
        if (value != null && !value.isBlank()) {
            joiner.add(value.trim());
        }
    }
}
