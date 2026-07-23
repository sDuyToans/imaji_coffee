package com.duytoan.imajicoffee.imaji_coffee_be.services.common;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.EventDto;

import java.util.List;

/**
 * Event interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IEventService {

    /**
     * Get upcoming events with page and size
     * @param page -> int
     * @param size -> int
     * @return List of EventDto Objects
     */
    List<EventDto> getPageUpComingEvent(int page, int size);

    /**
     * Get closed events with page and size
     * @param page -> int
     * @param size -> int
     * @return List of EventDto Objects
     */
    List<EventDto> getPageClosedEvent(int page, int size);
}
