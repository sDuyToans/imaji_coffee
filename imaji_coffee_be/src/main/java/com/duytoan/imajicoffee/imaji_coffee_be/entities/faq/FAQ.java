package com.duytoan.imajicoffee.imaji_coffee_be.entities.faq;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faqs")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FAQ extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long id;

    @Column(name = "question", nullable = false, unique = true)
    private String question;

    @Lob
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "category", length = 100, nullable = false)
    private String category;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

