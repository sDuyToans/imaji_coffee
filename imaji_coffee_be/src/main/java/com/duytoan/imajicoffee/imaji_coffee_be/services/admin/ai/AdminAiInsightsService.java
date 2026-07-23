package com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai.provider.AdminAiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAiInsightsService {

    private final AdminAnalyticsService analyticsService;
    private final AdminAiProvider aiProvider;
    private final AiRequestRateLimiter rateLimiter;
    private final AiSafetyGuard aiSafetyGuard;

    @Value("${ai.admin.timeout-ms:3000}")
    private long timeoutMs;

    public AdminDashboardSummaryDto getSummary() {
        return analyticsService.buildSummary();
    }

    public AdminAiQuestionResponseDto ask(Long adminUserId, String rawQuestion) {
        if (!rateLimiter.allow("ai-insights:ask:" + adminUserId)) {
            throw new IllegalArgumentException("Rate limit exceeded. Please wait before asking again.");
        }

        String sanitizedQuestion = aiSafetyGuard.sanitizeQuestion(rawQuestion);
        AdminDashboardSummaryDto summary = analyticsService.buildSummary();

        AdminAiProvider.AdminAiAnswer answer;
        try {
            answer = CompletableFuture
                    .supplyAsync(() -> aiProvider.answer(sanitizedQuestion, summary))
                    .get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("AI provider timeout/failure for admin {}: {}", adminUserId, e.getMessage());
            answer = new AdminAiProvider.AdminAiAnswer(
                    "The AI assistant is temporarily unavailable. Please retry shortly. This is an AI recommendation and may be incomplete.",
                    List.of("Fallback response due to timeout or unavailable provider."),
                    suggestedQuestions().questions()
            );
        }

        log.info(
                "AI admin question processed: adminId={}, questionLength={}, evidenceCount={}",
                adminUserId,
                sanitizedQuestion.length(),
                answer.evidence().size()
        );

        return new AdminAiQuestionResponseDto(
                answer.answer(),
                "AI Recommendation",
                answer.evidence(),
                answer.suggestedQuestions(),
                Instant.now()
        );
    }

    public AdminSuggestedQuestionsResponseDto suggestedQuestions() {
        return new AdminSuggestedQuestionsResponseDto(List.of(
                "What were our best-selling products this month?",
                "Which products may run out soon?",
                "Why did sales decrease this week?",
                "Which promo codes were used most often?"
        ));
    }
}
