package com.duytoan.imajicoffee.imaji_coffee_be.services.common;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.SpaceDto;

import java.util.List;


/**
 * Space interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface ISpaceService {

    /**
     * Get Spaces
     * @return List of SpaceDto Object
     */
    List<SpaceDto> getSpaces();

}
