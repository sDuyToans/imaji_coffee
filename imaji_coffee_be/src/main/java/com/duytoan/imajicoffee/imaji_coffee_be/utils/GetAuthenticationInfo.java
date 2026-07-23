package com.duytoan.imajicoffee.imaji_coffee_be.utils;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Utility class to get authentication information from the security context.
 * @author duytoan
 * @since 10/2025
 */
@Component
public class GetAuthenticationInfo {

    /**
     * Get user ID from the authentication object.
     * @return user ID as a Long
     */
    public Long getUserId() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}
