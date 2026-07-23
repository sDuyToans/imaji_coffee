package com.duytoan.imajicoffee.imaji_coffee_be.services.common.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.NewDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.common.News;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.common.NewRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.common.INewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implemented NewService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@AllArgsConstructor
@Service
public class NewServiceImpl implements INewService {

    private final NewRepository newRepository;

    /**
     * Get news by page and size
     * @param page -> int
     * @param size -> int
     * @return List of NewDto Object
     */
    @Override
    @Cacheable("news")
    public List<NewDto> getPageNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return newRepository.findAll(pageable).stream().map(this::mapToNewDto).collect(Collectors.toList());
    }

    /**
     * Get New By newId
     * @param newId -> long
     * @return NewDto Object
     */
    @Override
    public NewDto getNewById(Long newId) {
        return newRepository
                .findById(newId)
                .map(this::mapToNewDto)
                .orElseThrow(() -> new ResourceNotFoundException("New", "NewId", newId.toString()));
    }

    /**
     * Map New to NewDto Object
     * @param news -> new object
     * @return NewDto Object
     */
    private NewDto mapToNewDto(News news){
        NewDto newDto = new NewDto();
        BeanUtils.copyProperties(news, newDto);
        return newDto;
    }
}
