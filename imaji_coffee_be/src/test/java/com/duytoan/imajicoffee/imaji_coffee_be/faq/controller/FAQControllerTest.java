package com.duytoan.imajicoffee.imaji_coffee_be.faq.controller;


import com.duytoan.imajicoffee.imaji_coffee_be.controller.faq.FAQController;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQCreateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQUpdateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetailsService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.faq.IFAQService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FAQController.class, excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc
class FAQControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFAQService ifaqService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private AuditingHandler auditingHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void listFaqs_searchReturnsPagedDto() throws Exception {
        FAQ f = FAQ.builder().id(1L).question("Q").answer("A").category("c").isActive(true).build();
        when(ifaqService.searchFAQs(eq("Q"), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(f)));

        mockMvc.perform(get("/api/v1/faqs")
                .param("keyword", "Q")
                .param("page", "0")
                .param("size", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].question").value("Q"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFaq_returnsFaqDto() throws Exception {
        FAQ f = FAQ.builder().id(2L).question("who").answer("me").category("general").isActive(true).build();
        when(ifaqService.getFAQById(2L)).thenReturn(f);

        mockMvc.perform(get("/api/v1/faqs/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.question").value("who"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFaq_asAdmin_returnsCreated() throws Exception {
        FAQCreateRequest req = new FAQCreateRequest("Q1", "A1", "cate", true);
        FAQ saved = FAQ.builder().id(3L).question("Q1").answer("A1").category("cate").isActive(true).build();
        when(ifaqService.createFAQ(any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/faqs").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.question").value("Q1"));
    }

    @Test
    void createFaq_forbiddenNonAdmin() throws Exception {
        FAQCreateRequest req = new FAQCreateRequest("Q1", "A1", "cate", true);

        mockMvc.perform(post("/api/v1/faqs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFaq_asAdmin_returnsOk() throws Exception {
        FAQUpdateRequest req = new FAQUpdateRequest("New Q", "New A", "help", true);
        FAQ updated = FAQ.builder().id(1L).question("New Q").answer("New A").category("help").isActive(true).build();
        when(ifaqService.updateFAQ(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/faqs/1")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("New Q"));
    }
}
