package com.duytoan.imajicoffee.imaji_coffee_be.utils;

import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author duytoan
 * @since 01/13/2026
 */
@Component
@RequiredArgsConstructor
public class JwtCookieHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull  Map<String, Object> attributes){

        if (request instanceof ServletServerHttpRequest servletRequest) {

            HttpServletRequest req = servletRequest.getServletRequest();
            String uri = req.getRequestURI();

            // Allow sockJS boostrap endpoints (no auth)
            if (uri.endsWith("/info") || uri.endsWith("/iframe.html")) {
                return true;
            }

            // Allow CORS preflight
            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                return true;
            }

            Cookie[] cookies = req.getCookies();

            // Allow the websocket handshake to continue even when the browser only
            // sends the JWT in the STOMP CONNECT frame. The CONNECT interceptor
            // enforces authentication before any subscription or send happens.
            if (cookies == null || cookies.length == 0) {
                return true;
            }

            String token = null;
            for (Cookie cookie : cookies) {

                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }

            if (token == null || jwtUtil.isTokenExpired(token)){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            String username = jwtUtil.extractUserName(token);
            attributes.put("username", username);
            return true;

        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;

    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler, Exception exception) {

    }
}
