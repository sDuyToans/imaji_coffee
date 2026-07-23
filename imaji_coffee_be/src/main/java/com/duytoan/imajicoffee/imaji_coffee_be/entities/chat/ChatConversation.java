package com.duytoan.imajicoffee.imaji_coffee_be.entities.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatConversation Entity
 * @author duytoan
 * @since Apr 18 2026
 */
@Entity
@Table(name = "chat_conversation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "admin_id")
    private Long assignedAdminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status;

    @Column(name = "customer_last_read_message_id")
    private Long customerLastReadMessageId;

    @Column(name = "admin_last_read_message_id")
    private Long adminLastReadMessageId;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();
}
