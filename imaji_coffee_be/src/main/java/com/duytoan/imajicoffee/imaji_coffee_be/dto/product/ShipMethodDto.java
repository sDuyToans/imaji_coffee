package com.duytoan.imajicoffee.imaji_coffee_be.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipMethodDto {
    private Long methodId;
    private String methodName;
    private String code;
    private String expectedArrival;
    private BigDecimal price;
}
