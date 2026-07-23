package com.duytoan.imajicoffee.imaji_coffee_be.services.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatConversationResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatAdminNotificationResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageRequest;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.chat.ChatMessageResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatConversation;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatMessage;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ConversationStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderType;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderTypeDto;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.chat.ChatConversationRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the IChatService interface for managing chat conversations and messages.
 * This service handles the business logic for creating conversations, saving messages,
 * authorization, and admin load balancing for 1v1 customer-admin chat.
 *
 * @author Duy Toan
 * @since April 19, 2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AdminQueueAssignmentService adminQueueAssignmentService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Retrieves an existing open conversation for a given customer or creates a new one if none exists.
     * Auto-assigns to the least busy admin when created.
     */
    @Override
    @Transactional
    public ChatConversationResponse getOrCreateConversation(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId is required");
        }

        // Validate that the customer exists to avoid FK violations
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("User", "userId", customerId.toString());
        }

        return chatConversationRepository
                .findByCustomerIdAndStatusInOrderByUpdatedAtDesc(customerId, List.of(
                        ConversationStatus.OPEN,
                        ConversationStatus.WAITING,
                        ConversationStatus.PENDING
                ))
                .map(conversation -> {
                    if (conversation.getStatus() == ConversationStatus.WAITING) {
                        ChatConversation reassigned = adminQueueAssignmentService.assignConversationToLeastBusyAdmin(conversation);
                        if (reassigned.getStatus() != ConversationStatus.WAITING) {
                            return toChatConversationResponse(chatConversationRepository.save(reassigned));
                        }
                    }
                    return toChatConversationResponse(conversation);
                })
                .orElseGet(() -> createConversation(customerId));
    }

    @Override
    @Transactional
    public ChatConversationResponse getOrAssignAdminConversation(Long adminId) {
        if (adminId == null) {
            throw new IllegalArgumentException("adminId is required");
        }

        List<ConversationStatus> activeStatuses = List.of(ConversationStatus.OPEN, ConversationStatus.PENDING);

        return chatConversationRepository
                .findFirstByAssignedAdminIdAndStatusInOrderByUpdatedAtDesc(adminId, activeStatuses)
                .map(this::toChatConversationResponse)
                .orElseGet(() -> {
                    ChatConversation waitingConversation = chatConversationRepository
                            .findFirstByStatusOrderByCreatedAtAsc(ConversationStatus.WAITING)
                            .orElseThrow(() -> new ResourceNotFoundException("Chat conversation", "assignedAdminId", adminId.toString()));

                    waitingConversation.setAssignedAdminId(adminId);
                    waitingConversation.setStatus(ConversationStatus.PENDING);
                    waitingConversation.setUpdatedBy("SYSTEM");
                    waitingConversation.setUpdatedAt(Instant.now());

                    ChatConversation saved = chatConversationRepository.save(waitingConversation);
                    return toChatConversationResponse(saved);
                });
    }

    /**
     * Creates a new chat conversation for a given customer.
     * Automatically assigns to the least busy admin.
     */
    @Override
    @Transactional
    public ChatConversationResponse createConversation(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId is required");
        }

        // Validate that the customer exists before inserting a conversation (avoids FK error 1452)
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("User", "userId", customerId.toString());
        }

        ChatConversation conversation = ChatConversation.builder()
                .customerId(customerId)
                .status(ConversationStatus.OPEN)
                .build();
        conversation.setCreatedBy("SYSTEM");
        conversation.setUpdatedBy("SYSTEM");

        // Auto-assign to least busy admin
        ChatConversation saved = chatConversationRepository.save(conversation);
        ChatConversation assigned = adminQueueAssignmentService.assignConversationToLeastBusyAdmin(saved);
        ChatConversation finalConversation = chatConversationRepository.save(assigned);

        notifyAdminsAboutConversation(finalConversation);
        log.info("Created new conversation {} for customer {}, assigned to admin {}",
                finalConversation.getId(), customerId, finalConversation.getAssignedAdminId());

        return toChatConversationResponse(finalConversation);
    }

    /**
     * Saves a new chat message to a specific conversation.
     * Validates that the sender is authorized (customer or assigned admin).
     */
    @Override
    @Transactional
    public ChatMessageResponse saveMessage(Long conversationId, ChatMessageRequest request) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat conversation", "Conversation Id", conversationId.toString()));

        validateMessageRequest(request);

        // Authorization check: only customer or assigned admin can send
        if (!canUserSendMessage(conversationId, request.getSenderId())) {
            log.warn("Unauthorized message attempt: userId={}, conversationId={}", request.getSenderId(), conversationId);
            throw new IllegalArgumentException("User not authorized to send messages in this conversation");
        }

        // Prevent messages in closed conversations
        if (conversation.getStatus() == ConversationStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot send messages in a closed conversation");
        }

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .content(request.getContent())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderType(toEntitySenderType(request.getSenderType()))
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        markSenderReadMarker(conversation, request.getSenderId(), savedMessage.getId());

        if (conversation.getStatus() == ConversationStatus.PENDING
                || conversation.getStatus() == ConversationStatus.WAITING) {
            conversation.setStatus(ConversationStatus.OPEN);
        }

        // Update conversation timestamp
        conversation.setUpdatedAt(Instant.now());
        conversation.setUpdatedBy(request.getSenderName() != null ? request.getSenderName() : "SYSTEM");
        chatConversationRepository.save(conversation);

        routeMessageToConversationParticipants(conversationId, savedMessage);
        notifyMessageRecipient(conversationId, request, savedMessage);

        log.debug("Saved message {} in conversation {}", savedMessage.getId(), conversationId);
        return toChatMessageResponse(savedMessage);
    }

    /**
     * Retrieves all messages for a specific conversation, ordered by creation time.
     */
    @Override
    public List<ChatMessageResponse> getConversationMessages(Long conversationId) {
        return chatMessageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toChatMessageResponse)
                .toList();
    }

    @Override
    public Page<ChatMessageResponse> getConversationMessages(Long conversationId, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to zero");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return chatMessageRepository.findAllByConversationId(conversationId, pageable)
                .map(this::toChatMessageResponse);
    }

    /**
     * Retrieves all conversations for a specific customer, ordered by last updated time.
     */
    @Override
    public List<ChatConversationResponse> getCustomerConversations(Long customerId) {
        return chatConversationRepository.findAllByCustomerIdOrderByUpdatedAtDesc(customerId)
                .stream()
                .map(conversation -> toChatConversationResponse(conversation, ConversationViewer.CUSTOMER))
                .toList();
    }

    /**
     * Retrieves all conversations assigned to a specific admin, ordered by last updated time.
     */
    @Override
    public List<ChatConversationResponse> getAdminConversations(Long adminId) {
        List<ChatConversation> assignedConversations = chatConversationRepository
                .findAllByAssignedAdminIdOrderByUpdatedAtDesc(adminId);
        List<ChatConversation> activeConversations = chatConversationRepository
                .findAllByStatusIn(List.of(
                        ConversationStatus.OPEN,
                        ConversationStatus.PENDING,
                        ConversationStatus.WAITING
                ));

        Map<Long, ChatConversation> merged = new LinkedHashMap<>();
        assignedConversations.forEach(conversation -> merged.put(conversation.getId(), conversation));
        activeConversations.forEach(conversation -> merged.put(conversation.getId(), conversation));

        return merged.values().stream()
                .sorted(Comparator.comparing(ChatConversation::getUpdatedAt).reversed())
                .map(conversation -> toChatConversationResponse(conversation, ConversationViewer.ADMIN))
                .toList();
    }

    @Override
    @Transactional
    public ChatConversationResponse markConversationAsRead(Long conversationId, Long userId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat conversation", "Conversation Id", conversationId.toString()));

        if (!isAuthorizedUser(conversation, userId)) {
            throw new IllegalArgumentException("User not authorized to read this conversation");
        }

        ChatMessage latestMessage = chatMessageRepository.findTopByConversationIdOrderByIdDesc(conversationId).orElse(null);
        if (latestMessage == null) {
            return toChatConversationResponse(conversation);
        }

        if (conversation.getCustomerId().equals(userId)) {
            conversation.setCustomerLastReadMessageId(latestMessage.getId());
        } else if (conversation.getAssignedAdminId() != null && conversation.getAssignedAdminId().equals(userId)) {
            conversation.setAdminLastReadMessageId(latestMessage.getId());
        }

        conversation.setUpdatedAt(Instant.now());
        conversation.setUpdatedBy("SYSTEM");
        ChatConversation saved = chatConversationRepository.save(conversation);
        return toChatConversationResponse(saved);
    }

    /**
     * Closes a conversation and prevents further messages.
     * Only the customer or assigned admin can close.
     */
    @Override
    @Transactional
    public ChatConversationResponse closeConversation(Long conversationId, Long userId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat conversation", "Conversation Id", conversationId.toString()));

        // Only customer or assigned admin can close
        if (!isAuthorizedUser(conversation, userId)) {
            throw new IllegalArgumentException("User not authorized to close this conversation");
        }

        ChatConversation closed = adminQueueAssignmentService.closeConversation(conversation);
        log.info("Conversation {} closed by user {}", conversationId, userId);
        return toChatConversationResponse(chatConversationRepository.save(closed));
    }

    /**
     * Transfers a conversation to another admin (admin-only operation).
     */
    @Override
    @Transactional
    public ChatConversationResponse reassignConversation(Long conversationId, Long targetAdminId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat conversation", "Conversation Id", conversationId.toString()));

        Long oldAdminId = conversation.getAssignedAdminId();
        conversation.setAssignedAdminId(targetAdminId);
        conversation.setUpdatedBy("ADMIN_REASSIGN");
        conversation.setUpdatedAt(Instant.now());

        ChatConversation saved = chatConversationRepository.save(conversation);
        log.info("Conversation {} reassigned from admin {} to admin {}", conversationId, oldAdminId, targetAdminId);
        return toChatConversationResponse(saved);
    }

    /**
     * Gets the assigned admin ID for a conversation.
     */
    @Override
    public Long getAssignedAdminId(Long conversationId) {
        return chatConversationRepository.findById(conversationId)
                .map(ChatConversation::getAssignedAdminId)
                .orElse(null);
    }

    /**
     * Validates if a user can send a message in a conversation.
     * User must be either the customer or the assigned admin.
     */
    @Override
    public boolean canUserSendMessage(Long conversationId, Long userId) {
        return chatConversationRepository.findById(conversationId)
                .map(conv -> isAuthorizedUser(conv, userId))
                .orElse(false);
    }

    /**
     * Validates if a user can view a conversation.
     * User must be either the customer or the assigned admin.
     */
    @Override
    public boolean canUserViewConversation(Long conversationId, Long userId) {
        return chatConversationRepository.findById(conversationId)
                .map(conv -> isAuthorizedUser(conv, userId))
                .orElse(false);
    }

    // ======================== Helper Methods ========================

    /**
     * Checks if a user is authorized to interact with a conversation.
     * Authorized if: customer of the conversation OR assigned admin.
     */
    private boolean isAuthorizedUser(ChatConversation conversation, Long userId) {
        if (conversation.getCustomerId().equals(userId)) {
            return true; // Customer is always authorized
        }
        if (conversation.getAssignedAdminId() != null && conversation.getAssignedAdminId().equals(userId)) {
            return true; // Assigned admin is authorized
        }
        return false;
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderType(toDtoSenderType(message.getSenderType()))
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private void validateMessageRequest(ChatMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Message request is required");
        }

        String content = request.getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content is required");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("Message content must be 2000 characters or fewer");
        }

        String senderName = request.getSenderName();
        if (senderName == null || senderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender name is required");
        }
    }

    private ChatConversationResponse toChatConversationResponse(ChatConversation conversation) {
        return toChatConversationResponse(conversation, ConversationViewer.NONE);
    }

    private ChatConversationResponse toChatConversationResponse(ChatConversation conversation, ConversationViewer viewer) {
        Long queuePosition = conversation.getStatus() == ConversationStatus.WAITING
                ? adminQueueAssignmentService.getWaitingQueuePosition(conversation.getId())
                : null;
        String waitMessage = queuePosition != null
                ? "All admins are currently busy. You are in queue position " + queuePosition + "."
                : null;
        long messageCount = chatMessageRepository.countByConversationId(conversation.getId());
        long unreadCount = calculateUnreadCount(conversation, viewer);

        return ChatConversationResponse.builder()
                .id(conversation.getId())
                .customerId(conversation.getCustomerId())
                .assignedAdminId(conversation.getAssignedAdminId())
                .status(conversation.getStatus().name())
                .queuePosition(queuePosition)
                .waitMessage(waitMessage)
                .messageCount(messageCount)
                .unreadCount(unreadCount)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private long calculateUnreadCount(ChatConversation conversation, ConversationViewer viewer) {
        if (viewer == ConversationViewer.ADMIN) {
            long lastReadId = conversation.getAdminLastReadMessageId() == null ? 0L : conversation.getAdminLastReadMessageId();
            return chatMessageRepository.countByConversationIdAndIdGreaterThanAndSenderType(
                    conversation.getId(),
                    lastReadId,
                    SenderType.USER
            );
        }

        if (viewer == ConversationViewer.CUSTOMER) {
            long lastReadId = conversation.getCustomerLastReadMessageId() == null ? 0L : conversation.getCustomerLastReadMessageId();
            return chatMessageRepository.countByConversationIdAndIdGreaterThanAndSenderType(
                    conversation.getId(),
                    lastReadId,
                    SenderType.ADMIN
            );
        }

        return 0L;
    }

    private void markSenderReadMarker(ChatConversation conversation, Long senderId, Long messageId) {
        if (senderId == null || messageId == null) {
            return;
        }

        if (conversation.getCustomerId().equals(senderId)) {
            conversation.setCustomerLastReadMessageId(messageId);
        } else if (conversation.getAssignedAdminId() != null && conversation.getAssignedAdminId().equals(senderId)) {
            conversation.setAdminLastReadMessageId(messageId);
        }
    }

    private void notifyAdminsAboutConversation(ChatConversation conversation) {
        ChatAdminNotificationResponse payload = ChatAdminNotificationResponse.builder()
                .conversationId(conversation.getId())
                .notificationType(conversation.getAssignedAdminId() == null
                        ? "CONVERSATION_CREATED"
                        : "CONVERSATION_ASSIGNED")
                .senderId(conversation.getCustomerId())
                .senderName("System")
                .senderType(SenderTypeDto.ADMIN)
                .messagePreview(conversation.getStatus() == ConversationStatus.WAITING
                        ? "New customer conversation is waiting in the queue."
                        : "New customer conversation was assigned to an admin.")
                .createdAt(conversation.getUpdatedAt())
                .build();

        messagingTemplate.convertAndSend(
                "/api/v1/topic/chat/admin/notifications",
                payload
        );
    }

    private String toMessagePreview(String content) {
        if (content == null) {
            return "";
        }

        String trimmed = content.trim();
        if (trimmed.length() <= 120) {
            return trimmed;
        }
        return trimmed.substring(0, 117) + "...";
    }

    private SenderType toEntitySenderType(SenderTypeDto senderTypeDto) {
        return SenderType.valueOf(senderTypeDto.name());
    }

    private SenderTypeDto toDtoSenderType(SenderType senderType) {
        return SenderTypeDto.valueOf(senderType.name());
    }

    private enum ConversationViewer {
        NONE,
        CUSTOMER,
        ADMIN
    }

    private void routeMessageToConversationParticipants(Long conversationId, ChatMessage message) {
        try {
            Long customerId = chatConversationRepository.findById(conversationId)
                    .map(ChatConversation::getCustomerId)
                    .orElse(null);
            Long adminId = getAssignedAdminId(conversationId);

            String destination = "/queue/chat/messages/" + conversationId;

            if (customerId != null) {
                messagingTemplate.convertAndSendToUser(customerId.toString(), destination, toChatMessageResponse(message));
            }

            if (adminId != null) {
                messagingTemplate.convertAndSendToUser(adminId.toString(), destination, toChatMessageResponse(message));
            }
        } catch (Exception e) {
            log.error("Error routing message to participants", e);
        }
    }

    private void notifyMessageRecipient(Long conversationId, ChatMessageRequest request, ChatMessage savedMessage) {
        SenderTypeDto senderType = request.getSenderType();
        if (senderType == null) {
            return;
        }

        Long recipientUserId;
        String notificationType;
        if (senderType == SenderTypeDto.USER) {
            recipientUserId = getAssignedAdminId(conversationId);
            notificationType = "USER_MESSAGE";
        } else if (senderType == SenderTypeDto.ADMIN) {
            recipientUserId = chatConversationRepository.findById(conversationId)
                    .map(ChatConversation::getCustomerId)
                    .orElse(null);
            notificationType = "ADMIN_MESSAGE";
        } else {
            return;
        }

        if (recipientUserId == null) {
            if (senderType == SenderTypeDto.USER) {
                ChatAdminNotificationResponse broadcastPayload = ChatAdminNotificationResponse.builder()
                        .conversationId(conversationId)
                        .messageId(savedMessage.getId())
                        .notificationType("USER_MESSAGE_BROADCAST")
                        .senderId(savedMessage.getSenderId())
                        .senderName(savedMessage.getSenderName())
                        .senderType(toDtoSenderType(savedMessage.getSenderType()))
                        .messagePreview(toMessagePreview(savedMessage.getContent()))
                        .createdAt(savedMessage.getCreatedAt())
                        .build();

                messagingTemplate.convertAndSend(
                        "/api/v1/topic/chat/admin/notifications",
                        broadcastPayload
                );
            }
            return;
        }

        ChatAdminNotificationResponse payload = ChatAdminNotificationResponse.builder()
                .conversationId(conversationId)
                .messageId(savedMessage.getId())
                .notificationType(notificationType)
                .senderId(savedMessage.getSenderId())
                .senderName(savedMessage.getSenderName())
                .senderType(toDtoSenderType(savedMessage.getSenderType()))
                .messagePreview(toMessagePreview(savedMessage.getContent()))
                .createdAt(savedMessage.getCreatedAt())
                .build();

        messagingTemplate.convertAndSendToUser(
                recipientUserId.toString(),
                "/queue/chat/notifications",
                payload
        );
    }
}
