package com.duytoan.imajicoffee.imaji_coffee_be.services.common.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.SpaceDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.common.Space;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.common.SpaceRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.common.ISpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implemented SpaceService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements ISpaceService {
    private final SpaceRepository spaceRepository;

    /**
     * Get Spaces
     * @return List of SpaceDto Object
     */
    @Override
    @Cacheable(value = "spaces")
    public List<SpaceDto> getSpaces() {
        return spaceRepository.findAll()
                .stream().map(this::mapToSpaceDto)
                .collect(Collectors.toList());
    }


    /**
     * Map Space to SpaceDto Object
     * @param space -> space object
     * @return SpaceDto Object
     */
    private SpaceDto mapToSpaceDto(Space space) {
        SpaceDto spaceDto = new SpaceDto();
        BeanUtils.copyProperties(space, spaceDto);
        return spaceDto;
    }
}
