package com.duytoan.imajicoffee.imaji_coffee_be.config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor aware implementation, "who did what"
 * @author duytoan
 * @since 10/2025
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("SYSTEM");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String principalName) {
            if ("anonymousUser".equals(principalName)) {
                return Optional.of("SYSTEM");
            }
            return Optional.of(principalName);
        }

        return Optional.of(authentication.getName());
    }
}
