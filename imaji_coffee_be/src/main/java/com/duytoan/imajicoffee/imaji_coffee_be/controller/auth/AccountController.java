package com.duytoan.imajicoffee.imaji_coffee_be.controller.auth;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressCreateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressUpdateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.AccountOrderDetailResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UpdateProfileRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UserDto;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetails;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.IAddressService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.IOrderService;
import com.duytoan.imajicoffee.imaji_coffee_be.services.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Account controller
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final IAddressService addressService;
    private final IOrderService orderService;
    private final IUserService userService;

    /**
     * Get current user addresses
     * @param authentication -> authentication object
     * @return address list
     */
    @GetMapping("/address")
    public ResponseEntity<List<AddressDto>> getAddresses(Authentication authentication) {
        Long userId = currentUserId(authentication);
        List<AddressDto> addressDtoList = addressService.getAddressesForUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressDtoList);
    }

    /**
     * Create a new address for the authenticated user
     */
    @PostMapping("/address")
    public ResponseEntity<AddressDto> createAddress(
            Authentication authentication,
            @Valid @RequestBody AddressCreateRequestDto request
    ) {
        Long userId = currentUserId(authentication);
        AddressDto created = addressService.createAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing address
     */
    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressUpdateRequestDto request
    ) {
        Long userId = currentUserId(authentication);
        AddressDto updated = addressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete an address
     */
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Map<String, String>> deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId
    ) {
        Long userId = currentUserId(authentication);
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
    }

    /**
     * Set an address as default
     */
    @PatchMapping("/address/{addressId}/default")
    public ResponseEntity<AddressDto> setDefaultAddress(
            Authentication authentication,
            @PathVariable Long addressId
    ) {
        Long userId = currentUserId(authentication);
        AddressDto updated = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get current user orders
     * @param authentication -> auth object
     * @param status -> optional status filter
     * @return order list
     */
    @GetMapping("/orders")
    public ResponseEntity<List<AccountOrderDetailResponseDto>> getOrders(
            Authentication authentication,
            @RequestParam(required = false) String status
    ) {
        Long userId = currentUserId(authentication);
        List<AccountOrderDetailResponseDto> orderList = orderService.getAccountOrders(userId, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderList);
    }

    /**
     * Cancel an eligible order
     */
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderResponseDto> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        Long userId = currentUserId(authentication);
        var response = orderService.cancelOrder(orderId, userId, false, String.valueOf(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user info
     * @param authentication -> auth object
     * @return user dto
     */
    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        Long userId = currentUserId(authentication);
        UserDto userDto = userService.getUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    /**
     * Update current user profile
     * @param authentication -> auth object
     * @param request -> update profile request
     * @return updated user dto
     */
    @PatchMapping("/user")
    public ResponseEntity<UserDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequestDto request
    ) {
        Long userId = currentUserId(authentication);
        UserDto userDto = userService.updateProfile(userId, request);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Get user info from token saved in cookie
     * @param authentication -> auth object
     * @return Map of user information (username, email, roles)
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> myInfo(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getDetails();
        String userId = authentication.getName();

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "username", customUserDetails.getUsername(),
                "email", customUserDetails.getEmail(),
                "roles", customUserDetails.getAuthorities().toString()
        ));
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof String principalValue) {
            try {
                return Long.parseLong(principalValue);
            } catch (NumberFormatException ignored) {
                // fallback below
            }
        }
        return Long.parseLong(authentication.getName());
    }
}
