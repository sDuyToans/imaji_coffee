package com.duytoan.imajicoffee.imaji_coffee_be.services.product.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoAdminResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoUpsertRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoProduct;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoProductId;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IAdminPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminPromoServiceImpl implements IAdminPromoService {

    private final PromoRepository promoRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PromoAdminResponseDto> getAll() {
        return promoRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public PromoAdminResponseDto create(PromoUpsertRequestDto request, String actor) {
        promoRepository.findByCodeIgnoreCase(request.code()).ifPresent(existing -> {
            throw new IllegalArgumentException("Promo code already exists");
        });
        Promo promo = new Promo();
        applyUpsertFields(promo, request, actor, true);
        Promo saved = promoRepository.save(promo);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PromoAdminResponseDto update(Long promoId, PromoUpsertRequestDto request, String actor) {
        Promo promo = promoRepository.findById(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", "promoId", promoId.toString()));

        promoRepository.findByCodeIgnoreCase(request.code())
                .filter(existing -> !existing.getPromoId().equals(promoId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Promo code already exists");
                });

        applyUpsertFields(promo, request, actor, false);
        Promo saved = promoRepository.save(promo);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PromoAdminResponseDto setActive(Long promoId, boolean active, String actor) {
        Promo promo = promoRepository.findById(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", "promoId", promoId.toString()));
        promo.setIsActive(active);
        promo.setStatus(active ? "ACTIVE" : "INACTIVE");
        promo.setUpdatedAt(Instant.now());
        promo.setUpdatedBy(actor);
        return mapToResponse(promoRepository.save(promo));
    }

    @Override
    @Transactional
    public void delete(Long promoId) {
        Promo promo = promoRepository.findById(promoId)
                .orElseThrow(() -> new ResourceNotFoundException("Promo", "promoId", promoId.toString()));
        promoRepository.delete(promo);
    }

    private void applyUpsertFields(Promo promo, PromoUpsertRequestDto request, String actor, boolean isCreate) {
        validateUpsert(request);
        promo.setCode(request.code().trim().toUpperCase(Locale.ROOT));
        promo.setTitle(request.title());
        promo.setDescription(request.description());
        promo.setDiscountType(request.discountType().trim().toLowerCase(Locale.ROOT));
        promo.setDiscountValue(request.discountValue());
        promo.setStartAt(request.startAt());
        promo.setEndAt(request.endAt());
        promo.setIsActive(request.isActive() == null || request.isActive());
        promo.setStatus(request.status() == null || request.status().isBlank()
                ? (Boolean.TRUE.equals(promo.getIsActive()) ? "ACTIVE" : "INACTIVE")
                : request.status().trim().toUpperCase(Locale.ROOT));
        promo.setMinimumOrderAmount(request.minimumOrderAmount());
        promo.setMaxTotalUses(request.maxTotalUses());
        promo.setMaxUsesPerUser(request.maxUsesPerUser());
        promo.setEligibleCategory(request.eligibleCategory());
        promo.setRestrictedUserId(request.restrictedUserId());
        promo.setStackable(Boolean.TRUE.equals(request.stackable()));

        if (isCreate) {
            promo.setUsageCount(0);
            promo.setCreatedAt(Instant.now());
            promo.setCreatedBy(actor);
        } else {
            promo.setUpdatedAt(Instant.now());
            promo.setUpdatedBy(actor);
        }

        Set<PromoProduct> promoProducts = new LinkedHashSet<>();
        if (request.productIds() != null) {
            List<Product> products = productRepository.findAllById(request.productIds());
            if (products.size() != request.productIds().size()) {
                throw new IllegalArgumentException("One or more products do not exist");
            }
            for (Product product : products) {
                PromoProduct pp = new PromoProduct();
                PromoProductId id = new PromoProductId();
                id.setPromoId(promo.getPromoId());
                id.setProductId(product.getProductId());
                pp.setPromoProductId(id);
                pp.setPromo(promo);
                pp.setProduct(product);
                pp.setCreatedAt(Instant.now());
                pp.setCreatedBy(actor);
                promoProducts.add(pp);
            }
        }
        promo.getPromoProducts().clear();
        promo.getPromoProducts().addAll(promoProducts);
    }

    private void validateUpsert(PromoUpsertRequestDto request) {
        if (request.maxTotalUses() != null && request.maxTotalUses() < 0) {
            throw new IllegalArgumentException("Max total uses must be non-negative");
        }
        if (request.maxUsesPerUser() != null && request.maxUsesPerUser() < 0) {
            throw new IllegalArgumentException("Max uses per user must be non-negative");
        }
        String type = request.discountType().trim().toLowerCase(Locale.ROOT);
        if (!type.equals("percentage") && !type.equals("fixed") && !type.equals("free_shipping")) {
            throw new IllegalArgumentException("Unsupported promo discount type");
        }
        if (type.equals("percentage") && request.discountValue().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }
        if (request.startAt() != null && request.endAt() != null && request.endAt().isBefore(request.startAt())) {
            throw new IllegalArgumentException("Promo end date must be after start date");
        }
    }

    private PromoAdminResponseDto mapToResponse(Promo promo) {
        List<Long> productIds = promo.getPromoProducts().stream()
                .map(pp -> pp.getProduct().getProductId())
                .toList();
        return new PromoAdminResponseDto(
                promo.getPromoId(),
                promo.getCode(),
                promo.getTitle(),
                promo.getDescription(),
                promo.getDiscountType(),
                promo.getDiscountValue(),
                promo.getStartAt(),
                promo.getEndAt(),
                promo.getIsActive(),
                promo.getStatus(),
                promo.getMinimumOrderAmount(),
                promo.getMaxTotalUses(),
                promo.getMaxUsesPerUser(),
                promo.getUsageCount(),
                promo.getEligibleCategory(),
                promo.getRestrictedUserId(),
                promo.getStackable(),
                productIds,
                promo.getCreatedAt(),
                promo.getCreatedBy(),
                promo.getUpdatedAt(),
                promo.getUpdatedBy()
        );
    }
}
