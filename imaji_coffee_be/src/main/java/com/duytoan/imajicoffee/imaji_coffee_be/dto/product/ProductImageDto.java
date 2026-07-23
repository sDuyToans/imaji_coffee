package com.duytoan.imajicoffee.imaji_coffee_be.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {
    private Long productImageId;
    private String imageUrl;
    private Boolean isMain;
}
