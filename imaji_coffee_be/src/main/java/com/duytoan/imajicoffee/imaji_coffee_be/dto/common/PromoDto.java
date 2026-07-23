package com.duytoan.imajicoffee.imaji_coffee_be.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoDto {
    private Long promoId;;
    private String code;
    private String title;
    private String description;;
    private String discountType;
    private BigDecimal discountValue;
    private Instant startAt;
    private Instant endAt;
    private Boolean isActive;
}
