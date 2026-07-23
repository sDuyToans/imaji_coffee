package com.duytoan.imajicoffee.imaji_coffee_be.services.common;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.NewDto;

import java.util.List;


/**
 * News interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface INewService {

    /**
     * Get news by page and size
     * @param page -> int
     * @param size -> int
     * @return List of NewDto Object
     */
    List<NewDto> getPageNews(int page, int size);

    /**
     * Get New By newId
     * @param newId -> long
     * @return NewDto Object
     */
    NewDto getNewById(Long newId);
}
