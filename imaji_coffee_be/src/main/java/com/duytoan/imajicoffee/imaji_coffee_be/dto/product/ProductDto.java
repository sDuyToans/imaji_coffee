package com.duytoan.imajicoffee.imaji_coffee_be.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ProductDto {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private Boolean isAvailableAtWeb;;
    private Integer quantity;
    private String category;

    private Set<ProductImageDto> images;
}
