package com.duytoan.imajicoffee.imaji_coffee_be.repository.faq;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.faq.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {

    List<FAQ> findAllByIsActiveTrue();

    List<FAQ> findByCategory(String category);

    @Query(value = "SELECT * FROM faqs f WHERE f.is_active = true AND (LOWER(f.question) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(f.answer) LIKE CONCAT('%', LOWER(:keyword), '%'))", nativeQuery = true)
    Page<FAQ> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM faqs f WHERE (LOWER(f.question) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(f.answer) LIKE CONCAT('%', LOWER(:keyword), '%'))", nativeQuery = true)
    Page<FAQ> searchByKeywordAll(@Param("keyword") String keyword, Pageable pageable);

}
