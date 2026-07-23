package com.duytoan.imajicoffee.imaji_coffee_be.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    Long userId;
    String username;
    String email;
    String phone;
}
