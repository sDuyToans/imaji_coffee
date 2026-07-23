package com.duytoan.imajicoffee.imaji_coffee_be.repository.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatConversation;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    /**
     * Finds the most recent conversation for a given customer that has a status in the provided list of statuses.
     * @param customerId the ID of the customer
     * @param statuses   the list of conversation statuses to filter by
     * @return an Optional containing the most recent ChatConversation if found, or empty if no matching conversation exists
     */
    Optional<ChatConversation> findByCustomerIdAndStatusInOrderByUpdatedAtDesc(
            Long customerId,
            List<ConversationStatus> statuses
    );

    /**
     * Finds all conversations assigned to a specific admin, ordered by the last updated time in descending order.
     * @param assignedAdminId the ID of the assigned admin
     * @return a list of ChatConversation entities assigned to the specified admin, ordered by updated time
     */
    List<ChatConversation> findAllByAssignedAdminIdOrderByUpdatedAtDesc(Long assignedAdminId);

    /**
     * Counts active conversations for a specific admin (OPEN or PENDING status).
     * @param assignedAdminId the ID of the assigned admin
     * @return the count of active conversations
     */
    long countByAssignedAdminIdAndStatusIn(Long assignedAdminId, List<ConversationStatus> statuses);

    /**
     * Finds all conversations for a specific customer, ordered by the last updated time in descending order.
     * @param customerId the ID of the customer
     * @return a list of ChatConversation entities for the specified customer, ordered by updated time
     */
    List<ChatConversation> findAllByCustomerIdOrderByUpdatedAtDesc(Long customerId);

    /**
     * Finds the most recent conversation for a given customer with a specific status, ordered by the last updated time in descending order.
     * @param customerId the ID of the customer
     * @param conversationStatus the status of the conversation to filter by
     * @return an Optional containing the most recent ChatConversation if found, or empty if no matching conversation exists
     */
    Optional<ChatConversation> findFirstByCustomerIdAndStatusOrderByUpdatedAtDesc(Long customerId, ConversationStatus conversationStatus);

    long countByStatusAndIdLessThan(ConversationStatus status, Long id);

    /**
     * Finds all conversations with OPEN or PENDING status (unassigned or active).
     * @return a list of unassigned ChatConversation entities
     */
    List<ChatConversation> findAllByStatusIn(List<ConversationStatus> statuses);

    Optional<ChatConversation> findFirstByAssignedAdminIdAndStatusInOrderByUpdatedAtDesc(
            Long assignedAdminId,
            List<ConversationStatus> statuses
    );

    Optional<ChatConversation> findFirstByStatusOrderByCreatedAtAsc(ConversationStatus status);
}
