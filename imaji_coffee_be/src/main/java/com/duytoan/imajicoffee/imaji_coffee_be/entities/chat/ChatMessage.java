package com.duytoan.imajicoffee.imaji_coffee_be.entities.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Chat Message Entity
 * @author duytoan
 * @since 01/13/2026
 */
@Entity
@Table(name = "chat_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
