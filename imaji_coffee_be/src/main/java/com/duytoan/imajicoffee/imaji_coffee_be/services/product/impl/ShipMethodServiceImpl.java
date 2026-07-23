package com.duytoan.imajicoffee.imaji_coffee_be.services.product.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ShipMethodDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IShipMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implemented ShipMethodService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class ShipMethodServiceImpl implements IShipMethodService {

    private final ShipRepository shipRepository;

    /**
     * Get ship methods dto
     * @return list of ship method dto
     */
    @Override
    public List<ShipMethodDto> getShipMethods() {
        return shipRepository.findAllByIsActiveTrue()
                .stream().map(this::mapToShipMethodDto).toList();
    }

    /**
     * Map ship method to ship method dto
     * @param shipMethod -> object
     * @return ship method dto
     */
    private ShipMethodDto mapToShipMethodDto(Ship shipMethod){
        ShipMethodDto shipMethodDto = new ShipMethodDto();
        BeanUtils.copyProperties(shipMethod, shipMethodDto);
        return shipMethodDto;
    }
}
