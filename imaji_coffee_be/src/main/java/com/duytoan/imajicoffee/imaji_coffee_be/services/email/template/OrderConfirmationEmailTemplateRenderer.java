package com.duytoan.imajicoffee.imaji_coffee_be.services.email.template;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderConfirmationEmailTemplateRenderer {

    private static final String HTML_TEMPLATE_PATH = "templates/email/order-confirmation.html";
    private static final String TEXT_TEMPLATE_PATH = "templates/email/order-confirmation.txt";

    public String renderHtml(OrderConfirmationEmailModel model) {
        String htmlTemplate = loadTemplate(HTML_TEMPLATE_PATH);
        Map<String, String> values = commonValues(model);
        values.put("itemsRows", renderItemsRows(model));
        return applyTemplate(htmlTemplate, values);
    }

    public String renderText(OrderConfirmationEmailModel model) {
        String textTemplate = loadTemplate(TEXT_TEMPLATE_PATH);
        Map<String, String> values = commonValues(model);
        values.put("itemsTextRows", renderItemsTextRows(model));
        return applyTemplate(textTemplate, values);
    }

    private Map<String, String> commonValues(OrderConfirmationEmailModel model) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("customerName", escape(model.customerName()));
        values.put("orderNumber", escape(model.orderNumber()));
        values.put("orderDate", escape(model.orderDate()));
        values.put("paymentStatus", escape(model.paymentStatus()));
        values.put("estimatedDelivery", escape(model.estimatedDelivery()));
        values.put("shippingAddress", escape(model.shippingAddress()));
        values.put("paymentMethod", escape(model.paymentMethod()));
        values.put("subtotal", escape(model.subtotal()));
        values.put("discount", escape(model.discount()));
        values.put("tax", escape(model.tax()));
        values.put("shipping", escape(model.shipping()));
        values.put("total", escape(model.total()));
        values.put("currency", escape(model.currency()));
        values.put("viewOrderUrl", safeUrl(model.viewOrderUrl()));
        values.put("continueShoppingUrl", safeUrl(model.continueShoppingUrl()));
        values.put("contactSupportUrl", safeUrl(model.contactSupportUrl()));
        values.put("privacyPolicyUrl", safeUrl(model.privacyPolicyUrl()));
        values.put("termsUrl", safeUrl(model.termsUrl()));
        values.put("preferencesUrl", safeUrl(model.preferencesUrl()));
        values.put("brandLogoUrl", safeUrl(model.brandLogoUrl()));
        values.put("contactEmail", escape(model.contactEmail()));
        values.put("contactPhone", escape(model.contactPhone()));
        values.put("contactAddress", escape(model.contactAddress()));
        values.put("currentYear", escape(model.currentYear()));
        return values;
    }

    private String renderItemsRows(OrderConfirmationEmailModel model) {
        if (model.items() == null || model.items().isEmpty()) {
            return """
                    <tr>
                      <td colspan="4" style="padding: 16px; font-size: 14px; color: #6b7280; text-align: center;">
                        No order items available
                      </td>
                    </tr>
                    """;
        }

        return model.items().stream()
                .map(item -> """
                        <tr>
                          <td style="padding: 14px 10px; border-bottom: 1px solid #e5e7eb;">
                            <img src="%s" alt="Image for %s" width="56" height="56" style="display:block;border-radius:10px;object-fit:cover;">
                          </td>
                          <td style="padding: 14px 10px; border-bottom: 1px solid #e5e7eb; color: #111827; font-size: 14px;">%s</td>
                          <td style="padding: 14px 10px; border-bottom: 1px solid #e5e7eb; color: #374151; font-size: 14px; text-align:center;">%s</td>
                          <td style="padding: 14px 10px; border-bottom: 1px solid #e5e7eb; text-align:right; color:#111827; font-size:14px;">
                            <div>%s</div>
                            <div style="font-size:12px; color:#6b7280;">%s each</div>
                          </td>
                        </tr>
                        """.formatted(
                        safeUrl(item.imageUrl(), model.brandLogoUrl()),
                        escape(item.name()),
                        escape(item.name()),
                        escape(String.valueOf(item.quantity())),
                        escape(item.lineTotal()),
                        escape(item.unitPrice())
                ))
                .collect(Collectors.joining());
    }

    private String renderItemsTextRows(OrderConfirmationEmailModel model) {
        if (model.items() == null || model.items().isEmpty()) {
            return "No order items available";
        }
        return model.items().stream()
                .map(item -> "- %s x%s @ %s = %s".formatted(
                        normalize(item.name()),
                        item.quantity() == null ? "0" : item.quantity(),
                        normalize(item.unitPrice()),
                        normalize(item.lineTotal())
                ))
                .collect(Collectors.joining("\n"));
    }

    private String applyTemplate(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", normalize(entry.getValue()));
        }
        return result;
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load email template: " + path, e);
        }
    }

    private String escape(String value) {
        return HtmlUtils.htmlEscape(normalize(value));
    }

    private String safeUrl(String value) {
        return safeUrl(value, "#");
    }

    private String safeUrl(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return normalize(fallback);
        }
        String candidate = value.trim();
        String lower = candidate.toLowerCase();
        if (candidate.contains("\"") || candidate.contains("'") || candidate.contains("<") || candidate.contains(">") || candidate.contains(" ")) {
            return normalize(fallback);
        }
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return candidate;
        }
        return normalize(fallback);
    }

    private String normalize(String value) {
        return value == null ? "" : value;
    }
}
