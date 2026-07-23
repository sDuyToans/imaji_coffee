package com.duytoan.imajicoffee.imaji_coffee_be.controller.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.services.chat.IChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final IChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage/{conversationId}")
    public void sendMessage(
            @DestinationVariable("conversationId") Long conversationId,
            @Valid ChatMessageRequest request,
            Principal principal
    ) {
        Long senderId = resolveSenderId(request, principal);

        try {
            request.setSenderId(senderId);
            chatService.saveMessage(conversationId, request);
        } catch (IllegalArgumentException e) {
            log.warn("Chat send rejected: {}", e.getMessage());
            sendErrorToUser(senderId, "Not authorized: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing message for conversation {}", conversationId, e);
            sendErrorToUser(senderId, "Error processing message: " + e.getMessage());
        }
    }

    private Long resolveSenderId(ChatMessageRequest request, Principal principal) {
        Long requestSenderId = request.getSenderId();
        Long principalUserId = null;
        if (principal instanceof org.springframework.security.core.Authentication authentication) {
            principalUserId = extractPrincipalUserId(authentication.getPrincipal(), authentication.getName());
        } else if (principal != null) {
            principalUserId = parseUserId(principal.getName());
        }

        if (requestSenderId == null) {
            if (principalUserId == null) {
                throw new IllegalArgumentException("Sender id is required");
            }
            return principalUserId;
        }

        if (principalUserId != null && !principalUserId.equals(requestSenderId)) {
            throw new IllegalArgumentException("Sender id does not match authenticated user");
        }

        return requestSenderId;
    }

    private Long extractPrincipalUserId(Object authPrincipal, String authenticationName) {
        Long principalUserId = parseUserId(authPrincipal);
        if (principalUserId != null) {
            return principalUserId;
        }
        return parseUserId(authenticationName);
    }

    private Long parseUserId(Object value) {
        if (value instanceof Long userId) {
            return userId;
        }
        if (value instanceof String principalValue) {
            try {
                return Long.parseLong(principalValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private void sendErrorToUser(Long userId, String errorMessage) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/errors",
                    errorMessage
            );
        } catch (Exception e) {
            log.error("Could not send error message to user {}", userId, e);
        }
    }
}
