package com.duytoan.imajicoffee.imaji_coffee_be.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageResponse {
    private List<ProductDto> items;
    private int totalPages;
    private long totalElements;
}
