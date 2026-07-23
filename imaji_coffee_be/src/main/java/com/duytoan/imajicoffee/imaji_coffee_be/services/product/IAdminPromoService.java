package com.duytoan.imajicoffee.imaji_coffee_be.services.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoAdminResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoUpsertRequestDto;

import java.util.List;

public interface IAdminPromoService {
    List<PromoAdminResponseDto> getAll();

    PromoAdminResponseDto create(PromoUpsertRequestDto request, String actor);

    PromoAdminResponseDto update(Long promoId, PromoUpsertRequestDto request, String actor);

    PromoAdminResponseDto setActive(Long promoId, boolean active, String actor);

    void delete(Long promoId);
}
