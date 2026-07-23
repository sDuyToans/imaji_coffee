package com.duytoan.imajicoffee.imaji_coffee_be.controller.faq;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQCreateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQMapper;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQUpdateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import com.duytoan.imajicoffee.imaji_coffee_be.services.faq.IFAQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faqs")
public class FAQController {

    private final IFAQService faqService;

    /**
     * List FAQs / Search
     *     * If \"keyword\" is provided -> paged search (active only).                                                                                                                                                                ┃
     *     * If \"category\" provided -> list by category.                                                                                                                                                                            ┃
     *     * If \"active=true\" provided -> only active FAQs.                                                                                                                                                                         ┃
     *     * Otherwise returns all FAQs (admin use).
     */
    @GetMapping
    public ResponseEntity<?> listFAQs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (keyword != null && !keyword.isEmpty()) {
            Page<FAQ> result = faqService.searchFAQs(keyword, page, size);
            Page<FAQResponse> dtoPage = result.map(FAQMapper::toDto);
            return ResponseEntity.ok(dtoPage);
        }

        if (category != null && !category.isEmpty()) {
            List<FAQResponse> byCategory = faqService.getFAQsByCategory(category)
                    .stream()
                    .map(FAQMapper::toDto)
                    .toList();
            return ResponseEntity.ok(byCategory);
        }

        if (Boolean.TRUE.equals(active)){
            List<FAQResponse> activeFAQs = faqService.getActiveFAQs()
                    .stream()
                    .map(FAQMapper::toDto)
                    .toList();
            return ResponseEntity.ok(activeFAQs);
        }

        // admin/all
        List<FAQResponse> allFAQs = faqService.getAllFAQs().stream().map(FAQMapper::toDto).toList();
        return ResponseEntity.ok(allFAQs);
    }

    /**
     * Get single FAQ by ID
     */
    @GetMapping("/{faqId}")
    public ResponseEntity<FAQResponse> getFaq(@PathVariable Long faqId) {
        FAQ faq = faqService.getFAQById(faqId);
        return ResponseEntity.ok(FAQMapper.toDto(faq));
    }

    /**
     * Create FAQ (admin)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQResponse> createFaq(@RequestBody FAQCreateRequest request){
        var createdFaq = faqService.createFAQ(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(FAQMapper.toDto(createdFaq));
    }

    /**
     * Update FAQ (admin)
     */
    @PutMapping("/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQResponse> updateFaq(@PathVariable Long faqId, @RequestBody FAQUpdateRequest request){
        var updated = faqService.updateFAQ(faqId, request);
        return ResponseEntity.ok(FAQMapper.toDto(updated));
    }

    /**
     * Soft-delete FAQ (admin)
     */
    @DeleteMapping("/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long faqId){
        faqService.deleteFAQ(faqId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Toggle active state (admin)
     */
    @PatchMapping("/{faqId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQResponse> toggleFaq(@PathVariable Long faqId){
        var toggled = faqService.toggleFAQActive(faqId);
        return ResponseEntity.ok(FAQMapper.toDto(toggled));
    }
}
