package com.duytoan.imajicoffee.imaji_coffee_be.dto.cart;

public record CartItemRequestDto(
        Long productId,
        Integer quantity
) {

}
