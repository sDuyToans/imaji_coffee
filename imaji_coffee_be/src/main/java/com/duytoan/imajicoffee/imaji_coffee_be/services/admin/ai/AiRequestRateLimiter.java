package com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiRequestRateLimiter {

    private static final long WINDOW_SECONDS = 60;
    private final Map<String, CounterWindow> windows = new ConcurrentHashMap<>();

    @Value("${ai.admin.rate-limit-per-minute:20}")
    private int rateLimitPerMinute;

    public boolean allow(String key) {
        Instant now = Instant.now();
        CounterWindow updated = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.windowStart.plusSeconds(WINDOW_SECONDS).isBefore(now)) {
                return new CounterWindow(now, 1);
            }
            return new CounterWindow(existing.windowStart, existing.counter + 1);
        });
        return updated.counter <= rateLimitPerMinute;
    }

    private record CounterWindow(Instant windowStart, int counter) {
    }
}
