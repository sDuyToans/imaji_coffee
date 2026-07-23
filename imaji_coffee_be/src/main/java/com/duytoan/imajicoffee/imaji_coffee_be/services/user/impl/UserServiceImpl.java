package com.duytoan.imajicoffee.imaji_coffee_be.services.user.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UpdateProfileRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UserDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Address;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Role;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.UserRole;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.RoleName;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.address.AddressRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.RoleRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRoleRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Implemented IUserService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * Get User Info
     * @param userId -> long
     * @return UserDto Object
     */
    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", userId.toString())
        );

        return mapToUserDto(user, userId);
    }

    @Override
    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", userId.toString())
        );

        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setUsername(request.username());
        user.setEmail(request.email().toLowerCase());
        user.setPhone(request.phone());
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(user.getUsername());

        User updated = userRepository.save(user);
        return mapToUserDto(updated, updated.getUserId());
    }

    /**
     * Create User using 0Auth2
     * @param email -> string
     * @param username -> string
     * @return created User object
     */
    @Override
    public User createUserOAuth2(String email, String username) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User nUser = new User();
            nUser.setEmail(email);
            nUser.setUsername(username);
            nUser.setPassword(UUID.randomUUID().toString());
            nUser.setCreatedBy("GOOGLE");
            nUser.setCreatedAt(Instant.now());
            User savedUser = userRepository.save(nUser);

            Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(
                    () -> new ResourceNotFoundException("Role", "roleName", RoleName.ROLE_USER.toString()));


            UserRole userRole = new UserRole();
            userRole.setRole(defaultRole);
            userRole.setUser(savedUser);
            userRoleRepository.save(userRole);

            savedUser.setUserRoles(Set.of(userRole));

            return savedUser;
        });
    }

    /**
     * Map user object to user dto object
     * @param user -> object
     * @param userId -> long
     * @return UserDto object
     */
    private UserDto mapToUserDto(User user, Long userId) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);

        if (userDto.getPhone() == null || userDto.getPhone().isBlank()) {
            addressRepository.findByUser_UserId(userId)
                    .stream().filter(Address::isDefault).findFirst()
                    .ifPresent(address -> userDto.setPhone(address.getPhoneNumber()));
        }

        return userDto;
    }
}
