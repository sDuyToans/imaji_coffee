package com.duytoan.imajicoffee.imaji_coffee_be.services.email.template;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderConfirmationEmailTemplateRendererTest {

    @Test
    void renderHtml_escapesUserContentAndUsesSafeImageFallback() {
        OrderConfirmationEmailTemplateRenderer renderer = new OrderConfirmationEmailTemplateRenderer();
        OrderConfirmationEmailModel model = baseModel(List.of(
                new OrderConfirmationEmailItemModel(
                        "<script>alert(1)</script>",
                        1,
                        "$10.00",
                        "$10.00",
                        "javascript:alert('xss')"
                )
        ));

        String html = renderer.renderHtml(model);

        assertThat(html).contains("&lt;script&gt;alert(1)&lt;/script&gt;");
        assertThat(html).doesNotContain("javascript:alert");
        assertThat(html).contains("http://localhost:5173/logo/logo.png");
    }

    @Test
    void renderText_containsFallbackWhenNoItems() {
        OrderConfirmationEmailTemplateRenderer renderer = new OrderConfirmationEmailTemplateRenderer();
        OrderConfirmationEmailModel model = baseModel(List.of());

        String text = renderer.renderText(model);

        assertThat(text).contains("No order items available");
        assertThat(text).contains("Order number: #15");
    }

    private OrderConfirmationEmailModel baseModel(List<OrderConfirmationEmailItemModel> items) {
        return new OrderConfirmationEmailModel(
                "John",
                "#15",
                "Jan 1, 2026",
                "Paid",
                "3-5 days",
                "123 Bean St",
                "Card",
                "$20.00",
                "$1.00",
                "$2.00",
                "$4.00",
                "$25.00",
                "USD",
                "http://localhost:5173/completed-checkout/15",
                "http://localhost:5173/menu",
                "http://localhost:5173/chat",
                "http://localhost:5173/privacy-policy",
                "http://localhost:5173/terms",
                "http://localhost:5173/account/setting",
                "http://localhost:5173/logo/logo.png",
                "support@imajicoffee.com",
                "+1 800 123 456",
                "Seattle",
                "2026",
                items
        );
    }
}
