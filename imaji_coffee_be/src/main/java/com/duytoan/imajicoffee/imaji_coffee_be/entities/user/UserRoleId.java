package com.duytoan.imajicoffee.imaji_coffee_be.entities.user;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleId implements Serializable {
    private Long userId;
    private Long roleId;
}
