package com.duytoan.imajicoffee.imaji_coffee_be.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Public path config in which these path will not require authentication
 * @author duytoan
 * @since 10/2025
 */
@Configuration
public class PublicPathConfig {

    @Bean
    public List<String> publicPaths() {
        return List.of(
                "/api/v1/products/**",
                "/api/v1/events/**",
                "/api/v1/news/**",
                "/api/v1/spaces/**",
                "/api/v1/webhooks/**",
                "/api/v1/ship-methods/**",
                "/api/v1/auth/**",
                "/api/v1/chat/**",
                "/api/v1/ws/**",
                "/actuator/health",
                "/error"
        );
    }
}
