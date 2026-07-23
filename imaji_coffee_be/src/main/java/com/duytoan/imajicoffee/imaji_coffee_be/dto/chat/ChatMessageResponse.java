package com.duytoan.imajicoffee.imaji_coffee_be.dto.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderTypeDto;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long conversationId;
    private String content;
    private String senderName;
    private SenderTypeDto senderType;
    private Long senderId;
    private Instant createdAt;

}
