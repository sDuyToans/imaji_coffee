package com.duytoan.imajicoffee.imaji_coffee_be.dto.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.enums.SenderTypeDto;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {

    @NotBlank
    @Size(max = 2000)
    private String content;

    @NotBlank
    @Size(max = 100)
    private String senderName;

    @NotNull
    private SenderTypeDto senderType;
    private Long senderId;

}
