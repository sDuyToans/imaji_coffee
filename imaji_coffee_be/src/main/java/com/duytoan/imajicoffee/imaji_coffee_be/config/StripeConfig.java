package com.duytoan.imajicoffee.imaji_coffee_be.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Stripe config
 * @author duytoan
 * @since 10/2025
 */
@Configuration
@PropertySource("classpath:application.properties")
@Slf4j
public class StripeConfig {
    @Value("${stripe.secretKey}")
    private String apiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = apiKey;
        if (apiKey == null || apiKey.isBlank() || "dummy".equalsIgnoreCase(apiKey)) {
            log.warn("Stripe secret key is not configured. Set STRIPE_SECRET_KEY for card payments.");
        }
    }
}
