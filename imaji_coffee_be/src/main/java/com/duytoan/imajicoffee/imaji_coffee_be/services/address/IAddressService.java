package com.duytoan.imajicoffee.imaji_coffee_be.services.address;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressCreateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressUpdateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Address;

import java.util.List;

/**
 * Address interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IAddressService {

    /**
     * Save address for order
     * @param addressDto
     * @param userId
     * @return Address object
     */
    Address saveAddressForOder(AddressDto addressDto, Long userId);

    /**
     * Get addresses of user
     * @param userId
     * @return List of address dto object
     */
    List<AddressDto> getAddressesForUser(Long userId);

    /**
     * Create a new address for the authenticated user
     * @param userId -> authenticated user id
     * @param request -> create request
     * @return created AddressDto
     */
    AddressDto createAddress(Long userId, AddressCreateRequestDto request);

    /**
     * Update an existing address owned by the authenticated user
     * @param userId -> authenticated user id
     * @param addressId -> address id
     * @param request -> update request
     * @return updated AddressDto
     */
    AddressDto updateAddress(Long userId, Long addressId, AddressUpdateRequestDto request);

    /**
     * Delete an address owned by the authenticated user
     * @param userId -> authenticated user id
     * @param addressId -> address id
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * Set an address as default for the authenticated user
     * @param userId -> authenticated user id
     * @param addressId -> address id
     * @return updated AddressDto
     */
    AddressDto setDefaultAddress(Long userId, Long addressId);
}
