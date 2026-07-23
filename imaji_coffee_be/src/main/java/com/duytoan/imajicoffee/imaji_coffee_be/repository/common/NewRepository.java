package com.duytoan.imajicoffee.imaji_coffee_be.repository.common;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.common.News;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * New repository
 * @author duytoan
 * @since 10/2025
 */
public interface NewRepository extends JpaRepository<News, Long> {
}
