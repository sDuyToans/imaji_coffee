package com.duytoan.imajicoffee.imaji_coffee_be.entities.common;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "event")
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false, length = 500)
    private String image;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "duration", nullable = false)
    private LocalTime duration;

}
