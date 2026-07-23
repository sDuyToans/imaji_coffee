package com.duytoan.imajicoffee.imaji_coffee_be.entities.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "ship")
public class Ship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "methodId", nullable = false)
    private Long methodId;

    @Column(name = "methodName", nullable = false, length = 100)
    private String methodName;

    @Column(name = "code", nullable = false, length = 5)
    private String code;

    @Column(name = "expectedArrival", nullable = false, length = 100)
    private String expectedArrival;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

}
