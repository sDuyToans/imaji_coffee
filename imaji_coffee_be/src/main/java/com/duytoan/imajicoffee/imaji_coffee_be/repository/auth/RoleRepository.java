package com.duytoan.imajicoffee.imaji_coffee_be.repository.auth;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Role;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Role repository
 * @author duytoan
 * @since 10/2025
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Get role by role name
     * @param name
     * @return optional role object
     */
    Optional<Role> findByName(RoleName name);
}
