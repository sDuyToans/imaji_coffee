package com.duytoan.imajicoffee.imaji_coffee_be.repository.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatMessage;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByConversationIdOrderByCreatedAtAsc(Long conversationId);
    Page<ChatMessage> findAllByConversationId(Long conversationId, Pageable pageable);
    long countByConversationId(Long conversationId);
    long countByConversationIdAndIdGreaterThanAndSenderType(Long conversationId, Long id, SenderType senderType);
    Optional<ChatMessage> findTopByConversationIdOrderByIdDesc(Long conversationId);

    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.senderType = :senderType
              AND m.createdAt BETWEEN :from AND :to
            ORDER BY m.createdAt DESC
            """)
    List<ChatMessage> findRecentBySenderType(
            @Param("senderType") SenderType senderType,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );
}
