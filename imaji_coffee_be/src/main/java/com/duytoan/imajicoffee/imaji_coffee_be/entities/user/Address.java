package com.duytoan.imajicoffee.imaji_coffee_be.entities.user;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String street;

    @Column(nullable = false, length = 2) // ISO CODE
    private String city;

    @Column(length = 100)
    private String province;

    @Column(length = 20)
    private String postalCode;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(length = 20)
    private String phoneNumber;

    @Column
    private String apartment;

    @Column(nullable = false)
    private boolean isDefault = false;
}
