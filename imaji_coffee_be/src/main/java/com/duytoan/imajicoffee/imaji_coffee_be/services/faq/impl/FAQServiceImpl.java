package com.duytoan.imajicoffee.imaji_coffee_be.services.faq.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQCreateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQUpdateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.faq.FAQRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.faq.IFAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the IFAQService interface for managing FAQs (Frequently Asked Questions).
 * This service provides methods for retrieving, creating, updating, deleting, and searching FAQs.
 */
@Service
@RequiredArgsConstructor
public class FAQServiceImpl implements IFAQService {

    private final FAQRepository faqRepository;

    @Override
    public List<FAQ> getAllFAQs() {
        return faqRepository.findAll();
    }

    @Override
    public List<FAQ> getActiveFAQs() {
        return faqRepository.findAllByIsActiveTrue();
    }

    @Override
    public FAQ getFAQById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", "faqId", String.valueOf(id)));
    }

    @Override
    public Page<FAQ> searchFAQs(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (keyword == null || keyword.isEmpty()) {
            return faqRepository.findAll(pageable);
        }
        return faqRepository.searchByKeyword(keyword.trim(), pageable); // active-only search
    }

    @Override
    public List<FAQ> getFAQsByCategory(String category) {
        return faqRepository.findByCategory(category);
    }

    @Override
    public FAQ createFAQ(FAQCreateRequest request) {
        FAQ faq = FAQ.builder()
                .question(request.question())
                .answer(request.answer())
                .category(request.category())
                .isActive(request.isActive() == null ? Boolean.TRUE : request.isActive())
                .build();
        return faqRepository.save(faq);
    }

    @Override
    public FAQ updateFAQ(Long id, FAQUpdateRequest request) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", "faqId", String.valueOf(id)));

        if (request.question() != null) faq.setQuestion(request.question());
        if (request.answer() != null) faq.setAnswer(request.answer());
        if (request.category() != null) faq.setCategory(request.category());
        if (request.isActive() != null) faq.setIsActive(request.isActive());

        return faqRepository.save(faq);
    }

    @Override
    public void deleteFAQ(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", "faqId", String.valueOf(id)));
        faq.setIsActive(false);
        faqRepository.save(faq);
    }

    @Override
    public FAQ toggleFAQActive(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", "faqId", String.valueOf(id)));
        faq.setIsActive(!Boolean.TRUE.equals(faq.getIsActive()));
        return faqRepository.save(faq);
    }
}
