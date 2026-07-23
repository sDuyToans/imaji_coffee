package com.duytoan.imajicoffee.imaji_coffee_be.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserText {
    private String sender;
    private String content;
}
