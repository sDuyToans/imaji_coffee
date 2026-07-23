package com.duytoan.imajicoffee.imaji_coffee_be.services.product.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.common.PromoDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductPromosDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implemented PromoService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class PromoServiceImpl implements IPromoService {
    private final PromoRepository promoRepository;

    /**
     * Get promo object for product with productId
     * @param productId -> long
     * @return ProductPromoDto
     */
    @Override
    public ProductPromosDto getPromosForProduct(Long productId) {
        List<PromoDto> availablePromos = promoRepository
                .findTop3ByOrderByPromoIdAsc()
                .stream().map(this::mapToPromoDto).toList();
        List<PromoDto> unavailablePromos = promoRepository
                .findTop3ByOrderByPromoIdDesc()
                .stream().map(this::mapToPromoDto).toList();
        return new ProductPromosDto(productId, availablePromos, unavailablePromos);
    }

    /**
     * Map promo to promo dto
     * @param promo -> object
     * @return promo dto
     */
    private PromoDto mapToPromoDto(Promo promo) {
        PromoDto promoDto = new PromoDto();
        BeanUtils.copyProperties(promo, promoDto);
        return promoDto;
    }
}
