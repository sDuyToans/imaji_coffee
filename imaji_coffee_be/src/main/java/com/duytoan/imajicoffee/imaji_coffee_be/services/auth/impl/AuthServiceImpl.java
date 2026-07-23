package com.duytoan.imajicoffee.imaji_coffee_be.services.auth.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.LoginRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.PasswordRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.SignUpRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Role;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.UserRole;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.UserRoleId;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ActionType;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.RoleName;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SignupStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.RoleRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRoleRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetails;
import com.duytoan.imajicoffee.imaji_coffee_be.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implemented AuthService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtil jwtUtil;

    /**
     * Sign up user
     * @param signUpRequestDto -> sign up request dto object
     * @return status -> enum status
     */
    @Override
    public SignupStatus signup(SignUpRequestDto signUpRequestDto) {
        User existingUser = userRepository.findByUsername(signUpRequestDto.email()).orElse(null);

        // 1. If email exists -> check whether guest or full user
        if (existingUser != null){
            if (!existingUser.isGuest()) {
                // Already a registered user -> block sign up
                return SignupStatus.EMAIL_ALREADY_EXISTS;
            }
            // 2, Upgrade guest user to full account
            existingUser.setUsername(signUpRequestDto.username());
            existingUser.setPassword(passwordEncoder.encode(signUpRequestDto.password()));
            existingUser.setGuest(false);
            userRepository.save(existingUser);

            // Assign role if missing
            assignDefaultRoleIfMissingUser(existingUser);

            return SignupStatus.SIGNUP_SUCCESS;
        }

        // 3. Normal signup for brand-new user
        User user = new User();
        user.setEmail(signUpRequestDto.email());
        user.setUsername(signUpRequestDto.username());
        user.setPassword(passwordEncoder.encode(signUpRequestDto.password()));
        user.setGuest(false);
        user.setCreatedBy("System");
        userRepository.save(user);

        assignDefaultRoleIfMissingUser(user);
        return SignupStatus.SIGNUP_SUCCESS;
    }

    /**
     * Login user
     * @param loginRequestDto -> login dto object
     * @return String message
     */
    @Override
    public String login(LoginRequestDto loginRequestDto) {
        // Authenticate User
       Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.loginInput(), loginRequestDto.password())
        );
        // return JWT token
        return jwtUtil.generateToken((CustomUserDetails) authentication.getPrincipal());
    }

    /**
     * Change account password for the authenticated user.
     *
     * @param userId -> authenticated user id
     * @param requestDto -> PasswordRequestDto object
     * @return action enum type
     */
    @Override
    public ActionType changePassword(Long userId, PasswordRequestDto requestDto) {
        if (!requestDto.newPassword().equals(requestDto.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        if (requestDto.newPassword().equals(requestDto.currentPassword())) {
            throw new IllegalArgumentException("New password must differ from current password");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        if (isOauthOnlyUser(user)) {
            throw new IllegalArgumentException("OAuth accounts cannot change a local password. Please use your OAuth provider.");
        }

        if (!passwordEncoder.matches(requestDto.currentPassword(), user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
        Integer currentVersion = user.getTokenVersion();
        user.setTokenVersion((currentVersion == null ? 0 : currentVersion) + 1);
        user.setUpdatedAt(java.time.Instant.now());
        user.setUpdatedBy(user.getUsername());
        userRepository.save(user);
        return ActionType.SUCCESS;
    }

    private boolean isOauthOnlyUser(User user) {
        String password = user.getPassword();
        return "GOOGLE".equals(user.getCreatedBy())
                && password != null
                && password.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    /**
     * Assign default role if none
     * @param user -> user object
     */
    private void assignDefaultRoleIfMissingUser(User user) {
        if (!userRoleRepository.existsByUser_UserId(user.getUserId())) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleName.ROLE_USER.toString()));

            UserRole link = new UserRole();
            link.setRole(userRole);
            link.setUser(user);
            link.setId(new UserRoleId(user.getUserId(), userRole.getRoleId()));
            link.setCreatedBy("System");
            userRoleRepository.save(link);
        }
    }
}
