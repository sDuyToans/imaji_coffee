package com.duytoan.imajicoffee.imaji_coffee_be.utils;

import com.duytoan.imajicoffee.imaji_coffee_be.jwt.JwtUtil;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetails;
import com.duytoan.imajicoffee.imaji_coffee_be.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatStompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || token.isBlank()) {
            token = accessor.getFirstNativeHeader("authorization");
        }
        if (token == null || token.isBlank()) {
            token = accessor.getFirstNativeHeader("token");
        }

        if (token == null || token.isBlank()) {
            Principal existingUser = accessor.getUser();
            if (existingUser != null) {
                return message;
            }
            log.warn("Websocket CONNECT missing token and user principal");
            return message;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String username = jwtUtil.extractUserName(token);
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUser().getUserId().toString(),
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(userDetails);
            accessor.setUser(authentication);
        } catch (Exception e) {
            log.warn("Unable to bind websocket CONNECT auth, falling back to existing principal", e);
        }
        return message;
    }
}
