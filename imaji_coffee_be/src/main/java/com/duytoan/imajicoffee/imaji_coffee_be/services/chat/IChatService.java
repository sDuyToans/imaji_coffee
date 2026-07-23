package com.duytoan.imajicoffee.imaji_coffee_be.services.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatConversationResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing chat conversations and messages.
 * Handles 1v1 customer-admin chat with load balancing and authorization.
 *
 * @author Duy Toan
 * @since April 19, 2026
 */
public interface IChatService {

    /**
     * Retrieves an existing open conversation for a given customer or creates a new one if none exists.
     * Auto-assigns to the least busy admin.
     */
    ChatConversationResponse getOrCreateConversation(Long customerId);

    /**
     * Retrieves the current conversation for an admin.
     * Returns an assigned OPEN/PENDING conversation when available,
     * otherwise claims the oldest WAITING conversation.
     */
    ChatConversationResponse getOrAssignAdminConversation(Long adminId);

    /**
     * Creates a new chat conversation for a given customer.
     * Auto-assigns to the least busy admin.
     */
    ChatConversationResponse createConversation(Long customerId);

    /**
     * Saves a new chat message to a specific conversation.
     * Validates that the sender is authorized (customer or assigned admin).
     *
     * @param conversationId the conversation ID
     * @param request the message request
     * @return the saved message response
     */
    ChatMessageResponse saveMessage(Long conversationId, ChatMessageRequest request);

    /**
     * Retrieves all messages for a specific conversation, ordered by creation time.
     * Validates authorization before returning.
     */
    List<ChatMessageResponse> getConversationMessages(Long conversationId);

    /**
     * Retrieves a page of messages for a specific conversation.
     * Used for paginated history loading in the chat UI.
     */
    Page<ChatMessageResponse> getConversationMessages(Long conversationId, int page, int size);

    /**
     * Marks a conversation as read for the currently authenticated participant.
     */
    ChatConversationResponse markConversationAsRead(Long conversationId, Long userId);

    /**
     * Retrieves all conversations for a specific customer, ordered by last updated time.
     * Only visible to the customer and admins.
     */
    List<ChatConversationResponse> getCustomerConversations(Long customerId);

    /**
     * Retrieves all conversations assigned to a specific admin, ordered by last updated time.
     * Only admins can retrieve their assigned conversations.
     */
    List<ChatConversationResponse> getAdminConversations(Long adminId);

    /**
     * Closes a conversation and prevents further messages.
     * Only the assigned admin or customer can close.
     */
    ChatConversationResponse closeConversation(Long conversationId, Long userId);

    /**
     * Transfers a conversation to another admin.
     * Only for admin reassignment.
     */
    ChatConversationResponse reassignConversation(Long conversationId, Long targetAdminId);

    /**
     * Gets the assigned admin ID for a conversation.
     * Returns null if not yet assigned.
     */
    Long getAssignedAdminId(Long conversationId);

    /**
     * Validates if a user can send a message in a conversation.
     * User must be either the customer or the assigned admin.
     */
    boolean canUserSendMessage(Long conversationId, Long userId);

    /**
     * Validates if a user can view a conversation.
     * User must be either the customer or the assigned admin.
     */
    boolean canUserViewConversation(Long conversationId, Long userId);
}
