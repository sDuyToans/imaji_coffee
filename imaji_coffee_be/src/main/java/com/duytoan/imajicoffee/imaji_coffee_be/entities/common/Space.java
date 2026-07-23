package com.duytoan.imajicoffee.imaji_coffee_be.entities.common;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "space")
public class Space extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "image", nullable = false, length = 500)
    private String image;

}
