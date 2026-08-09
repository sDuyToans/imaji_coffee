package com.duytoan.imajicoffee.imaji_coffee_be.services.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which admins are currently online based on their WebSocket (STOMP) sessions.
 * An admin is considered online while they have at least one active chat socket connection
 * (i.e. the admin chat page is open).
 *
 * @author duytoan
 * @since 08/08/2026
 */
@Slf4j
@Service
public class AdminPresenceService {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    /** sessionId -> admin userId, for sessions held by admins */
    private final Map<String, Long> adminSessions = new ConcurrentHashMap<>();
    /** Set of admin userIds with at least one active session */
    private final Set<Long> onlineAdmins = ConcurrentHashMap.newKeySet();

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (!(user instanceof Authentication authentication)) {
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
        if (!isAdmin) {
            return;
        }

        try {
            Long adminId = Long.parseLong(authentication.getName());
            String sessionId = event.getMessage() != null
                    ? StompHeaderAccessor.wrap(event.getMessage()).getSessionId()
                    : null;
            if (sessionId != null) {
                adminSessions.put(sessionId, adminId);
            }
            onlineAdmins.add(adminId);
            log.debug("Admin {} connected (session {})", adminId, sessionId);
        } catch (NumberFormatException e) {
            log.warn("Unable to resolve admin id from websocket principal {}", authentication.getName());
        }
    }

    @EventListener
    public void onSessionDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Long adminId = sessionId != null ? adminSessions.remove(sessionId) : null;
        if (adminId == null) {
            return;
        }

        if (!adminSessions.containsValue(adminId)) {
            onlineAdmins.remove(adminId);
            log.debug("Admin {} is now offline", adminId);
        }
    }

    /**
     * @return true if at least one admin currently has an active chat socket connection
     */
    public boolean isAnyAdminOnline() {
        return !onlineAdmins.isEmpty();
    }
}
