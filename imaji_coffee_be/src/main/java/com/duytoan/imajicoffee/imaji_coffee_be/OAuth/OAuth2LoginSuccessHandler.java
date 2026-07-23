package com.duytoan.imajicoffee.imaji_coffee_be.OAuth;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetails;
import com.duytoan.imajicoffee.imaji_coffee_be.services.user.IUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * @author duytoan
 * @since 12/2025
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final IUserService userService;

    @Value("${app.frontend.redirect-url}")
    private String frontEndRedirectUrl;

    /**
     * Set JWT token to Cookie
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String username = (String) attributes.get("name");

        // Create user if not exist
        User user = userService.createUserOAuth2(email, username);

        // create JWT
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(customUserDetails);

        // Set token to cookie
        ResponseCookie cookie = ResponseCookie
                .from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(30))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.sendRedirect(frontEndRedirectUrl);

    }

}
