package com.duytoan.imajicoffee.imaji_coffee_be.faq.repository;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.faq.FAQRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FAQRepositoryTest {

    @MockitoBean
    private AuditingHandler auditingHandler;
    @Autowired
    private FAQRepository faqRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByIsActiveTrue_shouldReturnOnlyActiveFAQs(){
        FAQ active1 = FAQ.builder()
                .question("What is coffee?")
                .answer("A beverage")
                .category("basics")
                .isActive(true)
                .build();
        active1.setCreatedBy("test_user");
        FAQ inactive = FAQ.builder()
                .question("What is tea?")
                .answer("Not coffee")
                .category("basics")
                .isActive(false)
                .build();
        inactive.setCreatedBy("test_user");

        entityManager.persistAndFlush(active1);
        entityManager.persistAndFlush(inactive);
        entityManager.clear();

        var result = faqRepository.findAllByIsActiveTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("basics");
    }

    @Test
    void searchByKeyword_returnsPaginatedResults() {
        FAQ faq1 = FAQ.builder()
                .question("How to brew espresso?")
                .answer("Use 9 bar pressure.")
                .category("brewing")
                .isActive(true)
                .build();
        faq1.setCreatedBy("test_user");
        FAQ faq2 = FAQ.builder()
                .question("What is espresso?")
                .answer("A type of coffee.")
                .category("brewing")
                .isActive(true)
                .build();
        faq2.setCreatedBy("test_user");

        FAQ faq3 = FAQ.builder()
                .question("Best beans?")
                .answer("Dark roast.")
                .category("beans")
                .isActive(true)
                .build();
        faq3.setCreatedBy("test_user");

        entityManager.persistAndFlush(faq1);
        entityManager.persistAndFlush(faq2);
        entityManager.persistAndFlush(faq3);
        entityManager.clear();

        Page<FAQ> result = faqRepository.searchByKeyword("espresso", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getQuestion()).containsIgnoringCase("espresso");
    }

    @Test
    void searchByKeywordAll_ignoresActiveStatus() {
        FAQ active = FAQ.builder()
                .question("How to brew?")
                .answer("Use hot water.")
                .category("brewing")
                .isActive(true)
                .build();
        active.setCreatedBy("test_user");
        FAQ inactive = FAQ.builder()
                .question("How to store brew?")
                .answer("In fridge.")
                .category("brewing")
                .isActive(false)
                .build();
        inactive.setCreatedBy("test_user");
        entityManager.persistAndFlush(active);
        entityManager.persistAndFlush(inactive);
        entityManager.clear();

        Page<FAQ> result = faqRepository.searchByKeywordAll("brew", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchByKeyword_caseInsensitiveSearch() {
        FAQ faq = FAQ.builder()
                .question("UPPERCASE QUESTION")
                .answer("lowercase answer")
                .category("test")
                .isActive(true)
                .build();
        faq.setCreatedBy("test_user");
        entityManager.persistAndFlush(faq);
        entityManager.clear();

        // Test case-insensitive
        Page<FAQ> result1 = faqRepository.searchByKeyword("uppercase", PageRequest.of(0, 10));
        Page<FAQ> result2 = faqRepository.searchByKeyword("LOWERCASE", PageRequest.of(0, 10));

        assertThat(result1.getContent()).hasSize(1);
        assertThat(result2.getContent()).hasSize(1);
    }
}
