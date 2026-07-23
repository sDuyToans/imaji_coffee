package com.duytoan.imajicoffee.imaji_coffee_be.services.auth;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.LoginRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.PasswordRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.SignUpRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ActionType;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SignupStatus;

/**
 * Auth interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IAuthService {

    /**
     * Sign up user
     * @param signUpRequestDto
     * @return status
     */
    public SignupStatus signup(SignUpRequestDto signUpRequestDto);

    /**
     * Login user
     * @param loginRequestDto
     * @return String message
     */
    public String login(LoginRequestDto loginRequestDto);

    /**
     * Change account password for the authenticated user.
     * @param userId -> authenticated user id
     * @param requestDto -> PasswordRequestDto object
     * @return action enum type
     */
    ActionType changePassword(Long userId, PasswordRequestDto requestDto);
}
