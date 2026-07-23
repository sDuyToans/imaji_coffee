package com.duytoan.imajicoffee.imaji_coffee_be.dto.chat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatMessage}
 */
@Data
@Setter
@Getter
@Builder
public class ChatMessageDto {

    private String conversationId;
    private String senderType; // USER, ADMIN, AI
    private String senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

}
