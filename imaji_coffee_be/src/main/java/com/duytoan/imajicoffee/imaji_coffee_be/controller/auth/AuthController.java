package com.duytoan.imajicoffee.imaji_coffee_be.controller.auth;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.LoginRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.PasswordRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.SignUpRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ActionType;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SignupStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.services.auth.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/**
 * Auth controller
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;
    private final JwtUtil  jwtUtil;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    /**
     * Set token to cookie after logged in
     * @param requestDto -> login request dto
     * @return String Map of message say ok
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response) {

        String token = authService.login(requestDto);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite)
                .maxAge(Duration.ofDays(30))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(Map.of("message", "ok"));
    }

    /**
     * Sign up route
     * @param requestDto -> sign up request dto
     * @return Status Code and message which is "error" or "successfully created"
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpRequestDto requestDto) {
        SignupStatus status = authService.signup(requestDto);
        return switch (status) {
            case SIGNUP_SUCCESS -> ResponseEntity.ok().body(Map.of("message", "User Register Successfully"));
            case EMAIL_ALREADY_EXISTS -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email Already Exists"));
            case USERNAME_ALREADY_EXISTS ->
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Username Already Exists"));
        };
    }

    /**
     * Log out function
     * @param response -> http servlet res
     * @return message with content Log out successfully!
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie cookie  = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().body(Map.of("message", "Logout Successfully"));
    }

    /**
     * Testing route for checking if the login validate or not, this is for string token version return only, not the cookie one
     * @param request -> http servlet res
     * @return HttpStatus and is the token valid
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.isTokenExpired(token)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("valid", true));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
        }
    }

    /**
     * Change account password for the authenticated user
     * @param authentication -> auth object
     * @param requestDto -> PasswordRequestDto object
     * @return -> res entity obj
     */
    @PatchMapping
    public ResponseEntity<Map<String, String>> updatePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordRequestDto requestDto,
            HttpServletResponse response
    ) {
        Long userId = currentUserId(authentication);
        try {
            authService.changePassword(userId, requestDto);

            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .path("/")
                    .maxAge(0)
                    .sameSite(cookieSameSite)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Current password is incorrect"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
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
