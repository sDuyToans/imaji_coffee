package com.duytoan.imajicoffee.imaji_coffee_be.config.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.duytoan.imajicoffee.imaji_coffee_be.utils.JwtCookieHandshakeInterceptor;
import com.duytoan.imajicoffee.imaji_coffee_be.utils.ChatStompAuthChannelInterceptor;
import com.duytoan.imajicoffee.imaji_coffee_be.utils.UserHandshakeHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Web Socket Configuration for 1v1 Customer-Admin Chat
 *
 * Message Flow:
 * 1. Client connects to: /api/v1 (with SockJS fallback)
 * 2. Client sends to: /api/v1/app/chat.sendMessage/{conversationId}
 * 3. Client subscribes to: /user/queue/chat/messages/{conversationId}
 * 4. Messages delivered via private user queues (only to authorized participants)
 *
 * @author duytoan
 * @since 01/13/2026
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${imajicoffee.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    private final JwtCookieHandshakeInterceptor jwtCookieHandshakeInterceptor;
    private final ChatStompAuthChannelInterceptor chatStompAuthChannelInterceptor;
    private final UserHandshakeHandler userHandshakeHandler;

    private String[] parseAllowedOrigins() {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        if (origins.isEmpty()) {
            return new String[]{"http://localhost:5173", "http://localhost:3000"};
        }
        return origins.toArray(new String[0]);
    }

    /**
     * Configure the message broker for handling subscriptions and broadcasts.
     *
     * - enableSimpleBroker: Routes messages to subscribers on specified prefixes
     *   - /api/v1/topic: For public/broadcast messages
     *   - /api/v1/queue: For point-to-point messages
     *   - /user: For user-specific private messages
     *
     * - setApplicationDestinationPrefixes: Messages sent to /api/v1/app/* are routed to @MessageMapping handlers
     * - setUserDestinationPrefix: Configures prefix for user-specific destinations
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple in-memory broker for multiple message prefixes
        config.enableSimpleBroker("/api/v1/topic", "/api/v1/queue", "/user");

        // Set prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/api/v1/app");

        // Set prefix for user-specific destinations (e.g., /user/queue/...)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatStompAuthChannelInterceptor);
    }

    /**
     * Register STOMP (Simple Text Oriented Message Protocol) endpoints for WebSocket connections.
     *
     * Endpoint: /api/v1
     * - Clients connect using: new SockJS('http://localhost:8080/api/v1')
     * - Then upgrade to STOMP protocol
     * - withSockJS() provides WebSocket fallback for browsers without WebSocket support
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/ws")
                .addInterceptors(jwtCookieHandshakeInterceptor)
                .setHandshakeHandler(userHandshakeHandler)
                .setAllowedOrigins(parseAllowedOrigins())
                .withSockJS()
                .setStreamBytesLimit(512 * 1024) // 512KB per stream
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30000); // 30 seconds disconnect delay
    }
}
