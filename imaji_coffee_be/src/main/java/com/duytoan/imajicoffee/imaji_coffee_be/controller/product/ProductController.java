package com.duytoan.imajicoffee.imaji_coffee_be.controller.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductPageResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author duytoan
 * @since 10/2025
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    /**
     * Get products
     * @param size -> page size
     * @param category -> product category
     * @param page -> page number
     * @return -> res entity
     */
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        if (category != null) {
            ProductPageResponse response = productService.getProductsByCategory(category, page, size != null ? size : 16);
            log.info(response.toString());
            return ResponseEntity.ok(response);
        } else if (size != null) {
            return ResponseEntity.ok(productService.getProductBySize(size));
        } else {
            return ResponseEntity.ok(productService.getProducts());
        }
    }

    /**
     * Search product filter
     * @return -> res entity
     */
    @GetMapping("/search")
    public  ResponseEntity<ProductPageResponse> searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue =  "8") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ){
        ProductPageResponse productPageResponse = productService.searchProducts(category, page, size, search, maxPrice, sortBy, sortDirection);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPageResponse);
    }

    /**
     * Get product info by id
     * @param productId -> long product id
     * @return -> res entity
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        ProductDto productDto = productService.getProductById(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDto);
    }

    /**
     * Get product info by id
     * @param productId -> long product id
     * @return -> res entity
     */
    @Deprecated
    @GetMapping("/detail/{productId}")
    public ResponseEntity<ProductDto> getProductByDetail(@PathVariable Long productId) {
        ProductDto productDto = productService.getProductById(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDto);
    }

    /**
     * Get related product by current product info
     * @return -> res entity
     */
    @GetMapping("/related")
    public ResponseEntity<List<ProductDto>> getRelatedProducts(@RequestParam String category, @RequestParam Long excludedId, @RequestParam Integer size) {
        List<ProductDto> productDtoList = productService.getRelatedProducts(category, size, excludedId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDtoList);
    }
}
