package com.duytoan.imajicoffee.imaji_coffee_be.controller.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatConversationResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatAdminNotificationResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderTypeDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.chat.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Chat REST Controller
 * Handles HTTP REST endpoints for chat operations including conversation management
 * and retrieving chat history. WebSocket operations are handled by ChatController.
 *
 * @author duytoan
 * @since April 19, 2026
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final IChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/me/current")
    @PreAuthorize("hasRole('USER')")
    public ChatConversationResponse startMyConversation(Authentication authentication) {
        return chatService.createConversation(currentUserId(authentication));
    }

    @GetMapping("/me/current")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ChatConversationResponse getMyCurrentConversation(Authentication authentication) {
        Long userId = currentUserId(authentication);
        if (hasAdminRole(authentication)) {
            return chatService.getOrAssignAdminConversation(userId);
        }
        return chatService.getOrCreateConversation(userId);
    }

    @PostMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ChatMessageResponse sendMessage(
            @PathVariable Long conversationId,
            @RequestBody ChatMessageRequest request,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        if (!chatService.canUserSendMessage(conversationId, userId)) {
            throw new AccessDeniedException("Not authorized to send messages in this conversation");
        }
        request.setSenderId(userId);
        return chatService.saveMessage(conversationId, request);
    }

    /**
     * Create a new chat conversation for a customer
     * Auto-assigns to the least busy admin
     *
     * @param customerId the ID of the customer
     * @return the created chat conversation response
     */
    @PostMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ChatConversationResponse createConversation(@PathVariable Long customerId, Authentication authentication) {
        if (!hasAdminRole(authentication) && !currentUserId(authentication).equals(customerId)) {
            throw new AccessDeniedException("Not authorized to create a conversation for this customer");
        }
        return chatService.createConversation(customerId);
    }

    /**
     * Get all messages for a specific conversation
     *
     * @param conversationId the ID of the conversation
     * @return a list of chat message responses
     */
    @GetMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ChatMessageResponse> getMessages(@PathVariable Long conversationId, Authentication authentication) {
        Long userId = currentUserId(authentication);
        if (!chatService.canUserViewConversation(conversationId, userId)) {
            throw new AccessDeniedException("Not authorized to view this conversation");
        }
        return chatService.getConversationMessages(conversationId);
    }

    /**
     * Get a paginated slice of messages for a specific conversation.
     *
     * @param conversationId the ID of the conversation
     * @return a page of chat message responses
     */
    @GetMapping("/{conversationId}/messages/page")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<ChatMessageResponse> getMessagesPage(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        if (!chatService.canUserViewConversation(conversationId, userId)) {
            throw new AccessDeniedException("Not authorized to view this conversation");
        }
        return chatService.getConversationMessages(conversationId, page, size);
    }

    /**
     * Mark a conversation as read for the authenticated participant.
     *
     * @param conversationId the ID of the conversation
     * @return the updated chat conversation response
     */
    @PostMapping("/{conversationId}/read")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ChatConversationResponse markConversationAsRead(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {
        return chatService.markConversationAsRead(conversationId, currentUserId(authentication));
    }

    /**
     * Get all conversations for a specific customer
     *
     * @param customerId the ID of the customer
     * @return a list of chat conversation responses
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ChatConversationResponse> getCustomerConversations(@PathVariable Long customerId, Authentication authentication) {
        if (!hasAdminRole(authentication) && !currentUserId(authentication).equals(customerId)) {
            throw new AccessDeniedException("Not authorized to view this customer's conversations");
        }
        return chatService.getCustomerConversations(customerId);
    }

    /**
     * Get all conversations for a specific admin
     *
     * @param adminId the ID of the admin
     * @return a list of chat conversation responses
     */
    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ChatConversationResponse> getAdminConversations(@PathVariable Long adminId, Authentication authentication) {
        if (!currentUserId(authentication).equals(adminId)) {
            throw new AccessDeniedException("Not authorized to view this admin inbox");
        }
        return chatService.getAdminConversations(adminId);
    }

    /**
     * Get an existing open conversation for a customer or create a new one if none exists
     *
     * @param customerId the ID of the customer
     * @return the chat conversation response
     */
    @GetMapping("/customer/{customerId}/current")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ChatConversationResponse getOrCreateConversation(@PathVariable Long customerId, Authentication authentication) {
        if (!hasAdminRole(authentication) && !currentUserId(authentication).equals(customerId)) {
            throw new AccessDeniedException("Not authorized to access this customer's conversation");
        }
        return chatService.getOrCreateConversation(customerId);
    }

    /**
     * Close a conversation. Only the customer or assigned admin can close.
     *
     * @param conversationId the ID of the conversation to close
     * @param userId the ID of the user requesting the close
     * @return the closed conversation response
     */
    @PutMapping("/{conversationId}/close")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ChatConversationResponse closeConversation(@PathVariable Long conversationId, Authentication authentication) {
        ChatConversationResponse closed = chatService.closeConversation(conversationId, currentUserId(authentication));

        if (closed.getCustomerId() != null) {
            ChatAdminNotificationResponse payload = ChatAdminNotificationResponse.builder()
                    .conversationId(closed.getId())
                    .notificationType("CONVERSATION_CLOSED")
                    .senderId(currentUserId(authentication))
                    .senderName("System")
                    .senderType(SenderTypeDto.ADMIN)
                    .messagePreview("This conversation was marked as solved.")
                    .createdAt(closed.getUpdatedAt())
                    .build();

            messagingTemplate.convertAndSendToUser(
                    closed.getCustomerId().toString(),
                    "/queue/chat/notifications",
                    payload
            );
        }

        return closed;
    }

    /**
     * Reassign a conversation to another admin (admin-only operation)
     *
     * @param conversationId the ID of the conversation
     * @param targetAdminId the ID of the target admin
     * @return the reassigned conversation response
     */
    @PutMapping("/{conversationId}/reassign/{targetAdminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ChatConversationResponse reassignConversation(
            @PathVariable Long conversationId,
            @PathVariable Long targetAdminId
    ) {
        return chatService.reassignConversation(conversationId, targetAdminId);
    }

    /**
     * Get the assigned admin ID for a conversation
     *
     * @param conversationId the ID of the conversation
     * @return the admin ID, or null if not assigned
     */
    @GetMapping("/{conversationId}/admin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Long getAssignedAdmin(@PathVariable Long conversationId, Authentication authentication) {
        Long userId = currentUserId(authentication);
        if (!chatService.canUserViewConversation(conversationId, userId)) {
            throw new AccessDeniedException("Not authorized to view this conversation");
        }
        return chatService.getAssignedAdminId(conversationId);
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof String principalValue) {
            try {
                return Long.parseLong(principalValue);
            } catch (NumberFormatException ignored) {
                // fallback below
            }
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException ignored) {
            // handled by exception below
        }
        throw new IllegalStateException("Authenticated user id is unavailable");
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
