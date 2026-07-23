package com.duytoan.imajicoffee.imaji_coffee_be.jwt;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetails;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTokenVersionTest {

    private JwtUtil jwtUtil;
    private User user;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        String secret = "a-very-long-test-secret-key-that-is-at-least-32-bytes!";
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtUtil, "key", key);

        user = new User();
        user.setUserId(10L);
        user.setUsername("tester");
        user.setEmail("tester@example.com");
        user.setPassword("encoded-password");
        user.setTokenVersion(2);

        userDetails = new CustomUserDetails(user);
    }

    @Test
    void generateToken_shouldIncludeTokenVersion() {
        String token = jwtUtil.generateToken(userDetails);

        Integer version = jwtUtil.extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));

        assertThat(version).isEqualTo(2);
    }

    @Test
    void isTokenValid_shouldAcceptMatchingTokenVersion() {
        String token = jwtUtil.generateToken(userDetails);

        boolean valid = jwtUtil.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldRejectOutdatedTokenVersion() {
        String token = jwtUtil.generateToken(userDetails);

        user.setTokenVersion(3);
        boolean valid = jwtUtil.isTokenValid(token, userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_shouldAcceptLegacyTokenWithoutVersionClaim_whenVersionIsZero() {
        user.setTokenVersion(0);
        SecretKey key = (SecretKey) ReflectionTestUtils.getField(jwtUtil, "key");
        String legacyToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean valid = jwtUtil.isTokenValid(legacyToken, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldRejectLegacyTokenWithoutVersionClaim_afterPasswordChange() {
        user.setTokenVersion(1);
        SecretKey key = (SecretKey) ReflectionTestUtils.getField(jwtUtil, "key");
        String legacyToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean valid = jwtUtil.isTokenValid(legacyToken, userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_shouldAcceptNewTokenAfterSignInAgain() {
        user.setTokenVersion(4);
        String newToken = jwtUtil.generateToken(userDetails);

        boolean valid = jwtUtil.isTokenValid(newToken, userDetails);

        assertThat(valid).isTrue();
    }
}
