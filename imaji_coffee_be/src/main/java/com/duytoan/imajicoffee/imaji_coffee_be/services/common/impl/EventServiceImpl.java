package com.duytoan.imajicoffee.imaji_coffee_be.services.common.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.EventDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.common.Event;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.common.EventRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.common.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implemented EventService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;

    /**
     * Get upcoming events with page and size
     * @param page -> int
     * @param size -> int
     * @return List of EventDto Objects
     */
    @Override
    @Cacheable(value = "upcomingEvents")
    public List<EventDto> getPageUpComingEvent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findByStartDateAfterOrderByStartDateDesc(Instant.now(), pageable);
        return eventPage.getContent()
                .stream().map(this::mapToEventDto)
                .collect(Collectors.toList());
    }

    /**
     * Get closed events with page and size
     * @param page -> int
     * @param size -> int
     * @return List of EventDto Objects
     */
    @Override
    @Cacheable(value = "closedEvents")
    public List<EventDto> getPageClosedEvent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findByStartDateBeforeOrderByStartDateAsc(Instant.now(), pageable);
        return eventPage.getContent()
                .stream().map(this::mapToEventDto)
                .collect(Collectors.toList());
    }

    /**
     * Map Event to EventDto Object
     * @param event -> event object
     * @return EventDto Object
     */
    private EventDto mapToEventDto(Event event) {
        EventDto eventDto = new EventDto();
        BeanUtils.copyProperties(event, eventDto);
        return eventDto;
    }
}
