package com.duytoan.imajicoffee.imaji_coffee_be.entities.common;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "news")
public class News extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "new_id", nullable = false)
    private Long newId;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "image", nullable = false, length = 500)
    private String image;

}
