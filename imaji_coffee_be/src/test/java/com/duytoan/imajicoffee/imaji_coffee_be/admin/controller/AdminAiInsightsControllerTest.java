package com.duytoan.imajicoffee.imaji_coffee_be.admin.controller;

import com.duytoan.imajicoffee.imaji_coffee_be.controller.admin.AdminAiInsightsController;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.admin.ai.*;
import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetailsService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.admin.ai.AdminAiInsightsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminAiInsightsController.class, excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc
class AdminAiInsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminAiInsightsService adminAiInsightsService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockitoBean
    private AuditingHandler auditingHandler;

    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void summary_adminCanAccess() throws Exception {
        AdminDashboardSummaryDto summary = new AdminDashboardSummaryDto(
                "note",
                Instant.now(),
                List.of(new AdminMetricDto("Orders today", "10", "daily")),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new AdminFeedbackSummaryDto(0, "unknown", List.of(), "none")
        );
        when(adminAiInsightsService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/v1/admin/ai-insights/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics[0].label").value("Orders today"));
    }

    @Test
    void summary_unauthenticatedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/ai-insights/summary"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    @WithMockUser(username = "3", roles = "ADMIN")
    void ask_adminCanAccess() throws Exception {
        when(adminAiInsightsService.ask(anyLong(), eq("What were our best-selling products this month?")))
                .thenReturn(new AdminAiQuestionResponseDto(
                        "answer",
                        "AI Recommendation",
                        List.of("evidence"),
                        List.of("q1"),
                        Instant.now()
                ));

        mockMvc.perform(post("/api/v1/admin/ai-insights/ask")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new AdminAiQuestionRequestDto("What were our best-selling products this month?")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("answer"));
    }
}
