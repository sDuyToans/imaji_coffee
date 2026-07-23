package com.duytoan.imajicoffee.imaji_coffee_be.controller.auth;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.PasswordRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ActionType;
import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.services.auth.IAuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Authentication authentication;

    @BeforeEach
    void setUpAuthentication() {
        authentication = new UsernamePasswordAuthenticationToken(
                10L,
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void logout_shouldClearAuthCookie() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("token", ""))
                .andExpect(cookie().maxAge("token", 0));
    }

    private RequestPostProcessor withAuthentication() {
        return request -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return request;
        };
    }

    @Test
    void changePassword_shouldClearAuthCookie_onSuccess() throws Exception {
        when(authService.changePassword(eq(10L), any(PasswordRequestDto.class))).thenReturn(ActionType.SUCCESS);

        mockMvc.perform(patch("/api/v1/auth")
                        .with(withAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "oldPass",
                                  "newPassword": "newPass",
                                  "confirmPassword": "newPass"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(cookie().value("token", ""))
                .andExpect(cookie().maxAge("token", 0));
    }

    @Test
    void changePassword_shouldNotClearCookie_whenCurrentPasswordIncorrect() throws Exception {
        doThrow(new org.springframework.security.authentication.BadCredentialsException("Current password is incorrect"))
                .when(authService).changePassword(eq(10L), any(PasswordRequestDto.class));

        mockMvc.perform(patch("/api/v1/auth")
                        .with(withAuthentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "wrongPass",
                                  "newPassword": "newPass",
                                  "confirmPassword": "newPass"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("token"));
    }
}
