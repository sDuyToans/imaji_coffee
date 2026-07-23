package com.duytoan.imajicoffee.imaji_coffee_be.services.address.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressCreateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressUpdateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Address;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.address.AddressRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.IAddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Implemented AddressService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    /**
     * Save address for order
     * @param addressDto
     * @param userId
     * @return Address object
     */
    @Override
    @Transactional
    public Address saveAddressForOder(AddressDto addressDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", String.valueOf(userId)));

        var existingAddress = addressRepository.findIdenticalAddress(
                userId,
                addressDto.country(),
                addressDto.city(),
                addressDto.street(),
                addressDto.postalCode(),
                addressDto.apartment(),
                addressDto.phoneNumber()
        );

        if (existingAddress.isPresent()){
            Address address = existingAddress.get();
            address.setCreatedAt(Instant.now());
            address.setCreatedBy(user.getUsername());
            if (addressDto.isDefault()){
                address.setDefault(true);
                addressRepository.save(address);
            }
            return address;
        }


        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        address.setUser(user);
        address.setCreatedAt(Instant.now());
        address.setCreatedBy(user.getUsername());
        long count = addressRepository.countByUser_UserId(user.getUserId());
        if (count == 0 || address.isDefault()){
            addressRepository.clearDefaultForUser(user.getUserId());
            address.setDefault(true);
        }
        return addressRepository.save(address);
    }

    /**
     * Get addresses of user
     * @param userId
     * @return List of address dto object
     */
    @Override
    public List<AddressDto> getAddressesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        return addressRepository.findByUser_UserId(user.getUserId())
                .stream().map(this::mapToAddressDto)
                .toList();
    }

    @Override
    @Transactional
    public AddressDto createAddress(Long userId, AddressCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        Address address = new Address();
        BeanUtils.copyProperties(request, address);
        address.setUser(user);
        address.setCreatedAt(Instant.now());
        address.setCreatedBy(user.getUsername());

        long count = addressRepository.countByUser_UserId(user.getUserId());
        if (count == 0) {
            address.setDefault(true);
        } else if (Boolean.TRUE.equals(request.isDefault())) {
            addressRepository.clearDefaultForUser(user.getUserId());
            address.setDefault(true);
        } else {
            address.setDefault(false);
        }

        Address saved = addressRepository.save(address);
        return mapToAddressDto(saved);
    }

    @Override
    @Transactional
    public AddressDto updateAddress(Long userId, Long addressId, AddressUpdateRequestDto request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId.toString()));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to update this address");
        }

        address.setName(request.name());
        address.setCountry(request.country());
        address.setProvince(request.province());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setPostalCode(request.postalCode());
        address.setApartment(request.apartment());
        address.setPhoneNumber(request.phoneNumber());
        address.setUpdatedAt(Instant.now());
        address.setUpdatedBy(address.getUser().getUsername());

        if (Boolean.TRUE.equals(request.isDefault()) && !address.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
            address.setDefault(true);
        }

        return mapToAddressDto(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId.toString()));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to delete this address");
        }

        addressRepository.delete(address);

        if (address.isDefault()) {
            List<Address> remaining = addressRepository.findByUser_UserId(userId);
            if (!remaining.isEmpty()) {
                Address newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional
    public AddressDto setDefaultAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId.toString()));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to update this address");
        }

        if (!address.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
            address.setDefault(true);
            address.setUpdatedAt(Instant.now());
            address.setUpdatedBy(address.getUser().getUsername());
            addressRepository.save(address);
        }

        return mapToAddressDto(address);
    }

    /**
     * Map address to address dto
     * @param address
     * @return Address dto
     */
    private AddressDto mapToAddressDto(Address address) {
        AddressDto addressDto = new AddressDto(
                address.getAddressId(),
                address.getUser().getUserId(),
                address.getName(),
                address.getCountry(),
                address.getProvince(),
                address.getCity(),
                address.getStreet(),
                address.getPostalCode(),
                address.getApartment(),
                address.getPhoneNumber(),
                address.isDefault()
        );
        return addressDto;
    }
}
