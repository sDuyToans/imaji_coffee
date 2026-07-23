package com.duytoan.imajicoffee.imaji_coffee_be.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Cache configuration class for Caffeine cache
 * @author duytoan
 * @since 10/2025
 */
@Configuration
public class CaffeineCacheConfig {
    private Caffeine<Object, Object> cacheSpec() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(1000);
    }

    /**
     * Method defines name catch for each routes
     * @return CacheManager object
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(
                new CaffeineCache("products", cacheSpec().build()),
                new CaffeineCache("products_size", cacheSpec().build()),
                new CaffeineCache("products_category", cacheSpec().build()),
                new CaffeineCache("searchProducts", cacheSpec().build()),
                new CaffeineCache("news", cacheSpec().build()),
                new CaffeineCache("upcomingEvents", cacheSpec().build()),
                new CaffeineCache("closedEvents", cacheSpec().build()),
                new CaffeineCache("spaces", cacheSpec().build())
        ));
        return manager;
    }
}
