package com.duytoan.imajicoffee.imaji_coffee_be.controller.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoAdminResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.promo.PromoUpsertRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IAdminPromoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/promos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromoController {

    private final IAdminPromoService adminPromoService;

    @GetMapping
    public ResponseEntity<List<PromoAdminResponseDto>> getAll() {
        return ResponseEntity.ok(adminPromoService.getAll());
    }

    @PostMapping
    public ResponseEntity<PromoAdminResponseDto> create(
            @Valid @RequestBody PromoUpsertRequestDto request,
            org.springframework.security.core.Authentication authentication
    ) {
        return ResponseEntity.ok(adminPromoService.create(request, authentication.getName()));
    }

    @PutMapping("/{promoId}")
    public ResponseEntity<PromoAdminResponseDto> update(
            @PathVariable Long promoId,
            @Valid @RequestBody PromoUpsertRequestDto request,
            org.springframework.security.core.Authentication authentication
    ) {
        return ResponseEntity.ok(adminPromoService.update(promoId, request, authentication.getName()));
    }

    @PatchMapping("/{promoId}/active")
    public ResponseEntity<PromoAdminResponseDto> setActive(
            @PathVariable Long promoId,
            @RequestBody Map<String, Boolean> body,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean active = Boolean.TRUE.equals(body.get("active"));
        return ResponseEntity.ok(adminPromoService.setActive(promoId, active, authentication.getName()));
    }

    @DeleteMapping("/{promoId}")
    public ResponseEntity<Void> delete(@PathVariable Long promoId) {
        adminPromoService.delete(promoId);
        return ResponseEntity.noContent().build();
    }
}
