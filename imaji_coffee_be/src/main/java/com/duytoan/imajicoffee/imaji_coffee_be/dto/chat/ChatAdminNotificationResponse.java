package com.duytoan.imajicoffee.imaji_coffee_be.dto.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderTypeDto;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAdminNotificationResponse {

    private Long conversationId;
    private Long messageId;
    private String notificationType;
    private Long senderId;
    private String senderName;
    private SenderTypeDto senderType;
    private String messagePreview;
    private Instant createdAt;
}
