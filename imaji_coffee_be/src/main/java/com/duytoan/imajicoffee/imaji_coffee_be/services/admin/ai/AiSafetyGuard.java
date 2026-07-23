package com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class AiSafetyGuard {

    private static final List<String> BLOCK_PATTERNS = List.of(
            "ignore previous instructions",
            "reveal token",
            "show password",
            "drop table",
            "delete from",
            "grant admin",
            "make me admin",
            "bypass auth"
    );

    private static final Pattern SECRET_PATTERN = Pattern.compile(
            "(?i)(bearer\\s+[a-z0-9\\-_\\.]+|sk_[a-z0-9]+|pk_[a-z0-9]+|password\\s*[:=]\\s*\\S+)"
    );

    public String sanitizeQuestion(String question) {
        String normalized = question == null ? "" : question.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Question is required");
        }
        String lower = normalized.toLowerCase(Locale.ROOT);
        for (String blocked : BLOCK_PATTERNS) {
            if (lower.contains(blocked)) {
                throw new IllegalArgumentException("Question contains unsupported instructions");
            }
        }
        return SECRET_PATTERN.matcher(normalized).replaceAll("[REDACTED]");
    }
}
