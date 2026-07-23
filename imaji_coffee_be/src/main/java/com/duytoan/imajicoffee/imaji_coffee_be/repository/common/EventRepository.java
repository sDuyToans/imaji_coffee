package com.duytoan.imajicoffee.imaji_coffee_be.repository.common;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.common.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

/**
 * Event repository
 * @author duytoan
 * @since 10/2025
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Find available events order by date desc
     * @param instant
     * @param pageable
     * @return events page
     */
    Page<Event> findByStartDateAfterOrderByStartDateDesc(Instant instant, Pageable pageable);

    /**
     * Find unavailable events order by date asc
     * @param instant
     * @param pageable
     * @return events page
     */
    Page<Event> findByStartDateBeforeOrderByStartDateAsc(Instant instant, Pageable pageable);
}
