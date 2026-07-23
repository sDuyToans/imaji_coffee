package com.duytoan.imajicoffee.imaji_coffee_be.services.faq;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQCreateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQUpdateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing FAQs (Frequently Asked Questions).
 * Provides methods for retrieving, creating, updating, deleting, and searching FAQs.
 */
public interface IFAQService {
    List<FAQ> getAllFAQs();
    List<FAQ> getActiveFAQs();
    FAQ getFAQById(Long id);
    Page<FAQ> searchFAQs(String keyword, int page, int size);
    List<FAQ> getFAQsByCategory(String category);

    FAQ createFAQ(FAQCreateRequest request);
    FAQ updateFAQ(Long id, FAQUpdateRequest request);
    void deleteFAQ(Long id);
    FAQ toggleFAQActive(Long id);
}
