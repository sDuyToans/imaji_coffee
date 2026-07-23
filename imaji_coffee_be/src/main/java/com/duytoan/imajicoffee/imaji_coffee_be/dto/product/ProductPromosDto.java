package com.duytoan.imajicoffee.imaji_coffee_be.dto.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.PromoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromosDto {
    private Long productId;
    private List<PromoDto> availablePromos;
    private List<PromoDto> unavailablePromos;
}
