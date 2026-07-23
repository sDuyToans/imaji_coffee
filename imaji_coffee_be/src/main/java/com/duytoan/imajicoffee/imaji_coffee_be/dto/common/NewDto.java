package com.duytoan.imajicoffee.imaji_coffee_be.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class NewDto {
    private final String time = "4 Min";
    private Long newId;
    private String title;
    private String description;
    private String image;
    private Instant createdAt;
}
