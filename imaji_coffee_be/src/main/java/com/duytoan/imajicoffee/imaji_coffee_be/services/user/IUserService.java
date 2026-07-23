package com.duytoan.imajicoffee.imaji_coffee_be.services.user;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UpdateProfileRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UserDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;

/**
 * User interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IUserService {

    /**
     * Get User Info
     * @param userId -> long
     * @return UserDto Object
     */
    UserDto getUser(Long userId);

    /**
     * Update current user profile
     * @param userId -> long
     * @param request -> update profile request
     * @return updated UserDto
     */
    UserDto updateProfile(Long userId, UpdateProfileRequestDto request);

    /**
     * Create User using 0Auth2
     * @param email -> string
     * @param username -> string
     * @return created User object
     */
    User createUserOAuth2(String email, String username);
}
