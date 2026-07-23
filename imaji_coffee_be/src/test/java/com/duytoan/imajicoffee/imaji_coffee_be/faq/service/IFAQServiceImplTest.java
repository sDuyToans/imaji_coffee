package com.duytoan.imajicoffee.imaji_coffee_be.faq.service;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.faq.FAQUpdateRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.faq.FAQRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.faq.impl.FAQServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.*;

@ExtendWith(MockitoExtension.class)
public class IFAQServiceImplTest {

    @Mock
    private FAQRepository faqRepository;

    @InjectMocks
    private FAQServiceImpl faqService;

    private FAQ sampleFaq;

    @Captor
    private ArgumentCaptor<FAQ> faqCaptor;

    @BeforeEach
    void setUp() {
        sampleFaq = FAQ.builder()
                .id(1L)
                .question("What is X?")
                .answer("X is ...")
                .category("general")
                .isActive(true)
                .build();
    }

    @Test
    void getFAQById_found() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));
        FAQ found = faqService.getFAQById(1L);
        assertSame(sampleFaq, found);
    }

    @Test
    void updateFAQ_updatesFieldsAndSaves() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));
        when(faqRepository.save(any(FAQ.class))).thenAnswer(inv -> inv.getArgument(0));

        FAQUpdateRequest req = new FAQUpdateRequest("New Q", "New A", "help", false);
        FAQ updated = faqService.updateFAQ(1L, req);

        verify(faqRepository).save(faqCaptor.capture());
        FAQ saved = faqCaptor.getValue();
        assertEquals("New Q", saved.getQuestion());
        assertEquals("New A", saved.getAnswer());
        assertEquals("help", saved.getCategory());
        assertFalse(saved.getIsActive());
        assertEquals(saved.getQuestion(), updated.getQuestion());
    }

    @Test
    void deleteFAQ_softDeletes(){
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));
        when(faqRepository.save(any(FAQ.class))).thenAnswer(inv -> inv.getArgument(0));

        faqService.deleteFAQ(1L);

        verify(faqRepository).save(faqCaptor.capture());
        FAQ saved = faqCaptor.getValue();
        assertFalse(saved.getIsActive());
    }

    @Test
    void toggleFAQActive_flipsState(){
        sampleFaq.setIsActive(true);
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));
        when(faqRepository.save(any(FAQ.class))).thenAnswer(inv -> inv.getArgument(0));

        FAQ toggled = faqService.toggleFAQActive(1L);

        verify(faqRepository).save(faqCaptor.capture());
        assertFalse(faqCaptor.getValue().getIsActive());
        assertFalse(toggled.getIsActive());
    }

    @Test
    void searchFAQs_returnsPagedResults() {
        Page<FAQ> page = new PageImpl<>(List.of(sampleFaq));
        when(faqRepository.searchByKeyword(anyString(), any())).thenReturn(page);

        Page<FAQ> result = faqService.searchFAQs("X", 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(faqRepository).searchByKeyword(eq("X"), any());
    }
}
