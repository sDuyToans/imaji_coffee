//package com.duytoan.imajicoffee.imaji_coffee_be.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.simp.SimpMessageType;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
//import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
//
///**
// * @author duytoan
// * @since 01/13/2026
// */
//@Configuration
//@EnableWebSocketSecurity
//public class WebSocketSecurityConfig {
//
//    @Bean
//    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//
//        messages
//                .simpTypeMatchers(
//                        SimpMessageType.CONNECT,
//                        SimpMessageType.DISCONNECT,
//                        SimpMessageType.HEARTBEAT
//                ).permitAll()
//
//                .simpDestMatchers("/app/chat.announce").hasAnyRole("ADMIN")
//                .simpDestMatchers("/app/chat.private").hasAnyRole("USER", "ADMIN")
//
//                .simpSubscribeDestMatchers("/user/**").authenticated()
//                .simpSubscribeDestMatchers("/topic/**").authenticated()
//                .simpSubscribeDestMatchers("/queue/**").authenticated()
//
//                .anyMessage().denyAll();
//
//        return messages.build();
//    }
//
//
//}
