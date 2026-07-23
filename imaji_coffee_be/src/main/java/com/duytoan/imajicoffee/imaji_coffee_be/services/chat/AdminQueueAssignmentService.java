package com.duytoan.imajicoffee.imaji_coffee_be.services.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatConversation;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ConversationStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.RoleName;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRoleRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.chat.ChatConversationRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Service for managing admin queue assignment and load balancing.
 * Ensures conversations are distributed evenly among available admins.
 *
 * TODO: Integrate with UserRepository to get actual admin users with ADMIN role
 *
 * @author Duy Toan
 * @since May 24, 2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminQueueAssignmentService {

    private final ChatConversationRepository chatConversationRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * Assigns a conversation to the admin with the least active conversations.
     * This implements a load-balancing strategy to distribute chat load evenly.
     *
     * NOTE: Currently this method doesn't assign to any admin.
     * To implement: Query UserRepository for admins with ADMIN role.
     *
     * @param conversation the conversation to assign
     * @return the assigned conversation with admin ID set
     */
    public ChatConversation assignConversationToLeastBusyAdmin(ChatConversation conversation) {
        try {
            List<Long> adminIds = userRoleRepository.findAllByRole_Name(RoleName.ROLE_ADMIN).stream()
                    .map(UserRole::getUser)
                    .filter(Objects::nonNull)
                    .map(user -> user.getUserId())
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (adminIds.isEmpty()) {
                conversation.setStatus(ConversationStatus.WAITING);
                conversation.setUpdatedBy("SYSTEM");
                conversation.setUpdatedAt(Instant.now());
                log.debug("Conversation {} created without admin assignment because no admins are available", conversation.getId());
                return conversation;
            }

            Long leastBusyAdminId = findLeastBusyAdminId(adminIds);
            if (leastBusyAdminId == null) {
                log.debug("Conversation {} created without admin assignment because no least-busy admin could be resolved", conversation.getId());
                return conversation;
            }

            conversation.setAssignedAdminId(leastBusyAdminId);
            conversation.setStatus(ConversationStatus.PENDING);
            conversation.setUpdatedBy("SYSTEM");
            conversation.setUpdatedAt(Instant.now());
            log.debug("Conversation {} assigned to admin {}", conversation.getId(), leastBusyAdminId);
        } catch (Exception e) {
            log.error("Error assigning conversation to admin", e);
        }
        return conversation;
    }

    /**
     * Returns the current waiting position for a queued conversation.
     */
    public long getWaitingQueuePosition(Long conversationId) {
        return chatConversationRepository.countByStatusAndIdLessThan(ConversationStatus.WAITING, conversationId) + 1;
    }

    /**
     * Finds the admin ID with the least active conversations.
     *
     * @param adminIds list of admin IDs to check
     * @return the ID of the least busy admin, or null if all are at capacity
     */
    private Long findLeastBusyAdminId(List<Long> adminIds) {
        List<ConversationStatus> activeStatuses = List.of(ConversationStatus.OPEN, ConversationStatus.PENDING);

        Long leastBusyAdminId = null;
        long minConversationCount = Long.MAX_VALUE;

        for (Long adminId : adminIds) {
            long count = chatConversationRepository.countByAssignedAdminIdAndStatusIn(adminId, activeStatuses);
            if (count < minConversationCount) {
                minConversationCount = count;
                leastBusyAdminId = adminId;
            }
        }

        return leastBusyAdminId;
    }

    /**
     * Gets the count of active conversations for an admin.
     *
     * @param adminId the admin ID
     * @return count of active (OPEN or PENDING) conversations
     */
    public long getAdminActiveConversationCount(Long adminId) {
        List<ConversationStatus> activeStatuses = List.of(ConversationStatus.OPEN, ConversationStatus.PENDING);
        return chatConversationRepository.countByAssignedAdminIdAndStatusIn(adminId, activeStatuses);
    }

    /**
     * Closes a conversation and makes it unavailable for new messages.
     *
     * @param conversation the conversation to close
     * @return the closed conversation
     */
    public ChatConversation closeConversation(ChatConversation conversation) {
        conversation.setStatus(ConversationStatus.CLOSED);
        conversation.setUpdatedBy("SYSTEM");
        conversation.setUpdatedAt(Instant.now());
        conversation.setAssignedAdminId(null); // Release admin after closing
        log.info("Conversation {} closed", conversation.getId());
        return chatConversationRepository.save(conversation);
    }

    /**
     * Re-assigns a conversation to another admin (e.g., if current admin is unavailable).
     *
     * @param conversation the conversation to reassign
     * @return the reassigned conversation
     */
    public ChatConversation reassignConversation(ChatConversation conversation) {
        Long oldAdminId = conversation.getAssignedAdminId();
        conversation.setAssignedAdminId(null);
        ChatConversation reassigned = assignConversationToLeastBusyAdmin(conversation);
        log.info("Conversation {} reassigned from admin {} to admin {}",
                reassigned.getId(), oldAdminId, reassigned.getAssignedAdminId());
        return chatConversationRepository.save(reassigned);
    }
}
