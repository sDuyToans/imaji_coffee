package com.duytoan.imajicoffee.imaji_coffee_be.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
public class EventDto {
    private Long eventId;
    private String name;
    private String image;
    private LocalTime duration;
    private Instant startDate;
}
