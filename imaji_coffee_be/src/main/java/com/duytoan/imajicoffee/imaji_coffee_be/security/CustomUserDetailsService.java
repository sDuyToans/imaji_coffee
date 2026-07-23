package com.duytoan.imajicoffee.imaji_coffee_be.security;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom user detail service implement user details service class
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Adjust the method to use user repository -> find user
     * @param loginInput the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(loginInput)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginInput));

        return new CustomUserDetails(user);
    }
}
