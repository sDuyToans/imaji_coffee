package com.duytoan.imajicoffee.imaji_coffee_be.repository.auth;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User repository
 * @author duytoan
 * @since 10/2025
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Get user by email
     * @param email
     * @return optional user object
     */
    Optional<User> findByEmail(String email);

    /**
     * Get user by username
     * @param username
     * @return optional user object
     */
    Optional<User> findByUsername(String username);

    /**
     * Get user by username or email
     * @param username
     * @param email
     * @return optional user object
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
}
