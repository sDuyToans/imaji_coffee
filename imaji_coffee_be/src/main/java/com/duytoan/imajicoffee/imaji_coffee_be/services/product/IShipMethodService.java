package com.duytoan.imajicoffee.imaji_coffee_be.services.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ShipMethodDto;

import java.util.List;

/**
 * Ship method interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IShipMethodService {

    /**
     * Get ship methods dto
     * @return list of ship method dto
     */
    List<ShipMethodDto> getShipMethods();
}
