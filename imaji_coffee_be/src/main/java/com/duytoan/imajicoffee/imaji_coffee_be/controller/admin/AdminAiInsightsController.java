package com.duytoan.imajicoffee.imaji_coffee_be.controller.admin;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.*;
import com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai.AdminAiInsightsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/ai-insights")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAiInsightsController {

    private final AdminAiInsightsService adminAiInsightsService;

    @GetMapping("/summary")
    public ResponseEntity<AdminDashboardSummaryDto> getSummary() {
        return ResponseEntity.ok(adminAiInsightsService.getSummary());
    }

    @GetMapping("/suggested-questions")
    public ResponseEntity<AdminSuggestedQuestionsResponseDto> getSuggestedQuestions() {
        return ResponseEntity.ok(adminAiInsightsService.suggestedQuestions());
    }

    @PostMapping("/ask")
    public ResponseEntity<AdminAiQuestionResponseDto> ask(
            @Valid @RequestBody AdminAiQuestionRequestDto request,
            Authentication authentication
    ) {
        Long adminUserId = currentUserId(authentication);
        return ResponseEntity.ok(adminAiInsightsService.ask(adminUserId, request.question()));
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof String principalValue) {
            try {
                return Long.parseLong(principalValue);
            } catch (NumberFormatException ignored) {
                // fallback below
            }
        }
        return Long.parseLong(authentication.getName());
    }
}
