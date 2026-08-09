package com.duytoan.imajicoffee.imaji_coffee_be.services.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.UserRole;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.RoleName;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;
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
@RequiredArgsConstructor
public class AdminPresenceService {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final UserRoleRepository userRoleRepository;

    /** sessionId -> admin userId, for sessions held by admins */
    private final Map<String, Long> adminSessions = new ConcurrentHashMap<>();
    /** Set of admin userIds with at least one active session */
    private final Set<Long> onlineAdmins = ConcurrentHashMap.newKeySet();

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user == null) {
            return;
        }

        Long adminId = resolveAdminId(user);
        if (adminId == null) {
            return;
        }

        String sessionId = event.getMessage() != null
                ? StompHeaderAccessor.wrap(event.getMessage()).getSessionId()
                : null;
        if (sessionId != null) {
            adminSessions.put(sessionId, adminId);
        }
        onlineAdmins.add(adminId);
        log.debug("Admin {} connected (session {})", adminId, sessionId);
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

    /**
     * Resolves the connected principal to an admin userId, or null if the user is not an admin.
     * The websocket principal is usually a simple principal carrying only the userId as its name
     * (set during the handshake), so roles are verified against the database.
     */
    private Long resolveAdminId(Principal user) {
        if (user instanceof Authentication authentication) {
            boolean hasAdminAuthority = authentication.getAuthorities().stream()
                    .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
            if (!hasAdminAuthority) {
                return null;
            }
        }

        Long userId;
        try {
            userId = Long.parseLong(user.getName());
        } catch (NumberFormatException e) {
            log.warn("Unable to resolve user id from websocket principal {}", user.getName());
            return null;
        }

        // If the principal carried no authorities (handshake principal), verify the role in the DB
        if (!(user instanceof Authentication)) {
            boolean isAdmin = userRoleRepository.findAllByRole_Name(RoleName.ROLE_ADMIN).stream()
                    .map(UserRole::getUser)
                    .filter(Objects::nonNull)
                    .anyMatch(u -> userId.equals(u.getUserId()));
            if (!isAdmin) {
                return null;
            }
        }

        return userId;
    }
}
