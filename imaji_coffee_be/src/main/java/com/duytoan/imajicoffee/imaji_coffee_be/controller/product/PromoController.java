package com.duytoan.imajicoffee.imaji_coffee_be.controller.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductPromosDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IPromoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class PromoController {
    private final IPromoService promoService;

    /**
     * Get promos by product id
     * @param productId -> long product id
     * @return -> res entity
     */
    @GetMapping("/{productId}/promos")
    public ResponseEntity<ProductPromosDto> getPromos(@PathVariable Long productId) {
        ProductPromosDto productPromosDto = promoService.getPromosForProduct(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPromosDto);
    }
}
