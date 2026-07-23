package com.duytoan.imajicoffee.imaji_coffee_be.controller.payment;

import com.duytoan.imajicoffee.imaji_coffee_be.services.order.IOrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {
    private final IOrderService orderService;
    private final ObjectMapper objectMapper;

    @Value("${stripe.webhook.secretKey}")
    private String stripeWebhookSecret;

    @Value("${paypal.webhook.secretKey:}")
    private String paypalWebhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
            String paymentIntentId = null;
            String externalPaymentId = null;

            if ("payment_intent.succeeded".equals(event.getType())
                    || "payment_intent.payment_failed".equals(event.getType())
                    || "payment_intent.canceled".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
                paymentIntentId = paymentIntent.getId();
                externalPaymentId = paymentIntent.getLatestCharge();
            } else if ("charge.refunded".equals(event.getType())) {
                Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElseThrow();
                paymentIntentId = charge.getPaymentIntent();
                externalPaymentId = charge.getId();
            }

            orderService.handleStripeWebhookEvent(event.getId(), event.getType(), paymentIntentId, externalPaymentId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("Stripe webhook failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid webhook");
        }
    }

    @PostMapping("/paypal")
    public ResponseEntity<String> handlePayPalEvent(
            @RequestBody String payload,
            @RequestHeader(value = "X-PayPal-Webhook-Secret", required = false) String providedSecret
    ) {
        try {
            if (paypalWebhookSecret != null && !paypalWebhookSecret.isBlank()) {
                if (providedSecret == null || !paypalWebhookSecret.equals(providedSecret)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid webhook secret");
                }
            }

            JsonNode root = objectMapper.readTree(payload);
            String eventId = root.path("id").asText(null);
            String eventType = root.path("event_type").asText(null);

            JsonNode resource = root.path("resource");
            Long orderId = parseOrderId(
                    resource.path("custom_id").asText(null),
                    resource.path("invoice_id").asText(null),
                    resource.path("supplementary_data").path("related_ids").path("invoice_id").asText(null)
            );
            String externalPaymentId = resource.path("id").asText(null);

            orderService.handlePayPalWebhookEvent(eventId, eventType, orderId, externalPaymentId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("PayPal webhook failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid webhook");
        }
    }

    private Long parseOrderId(String... candidates) {
        for (String candidate : candidates) {
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            try {
                return Long.parseLong(candidate.trim());
            } catch (NumberFormatException ignored) {
                // keep checking candidates
            }
        }
        return null;
    }
}
