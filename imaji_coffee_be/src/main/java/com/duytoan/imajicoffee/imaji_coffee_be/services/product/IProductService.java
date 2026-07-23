package com.duytoan.imajicoffee.imaji_coffee_be.services.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductPageResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IProductService {

    /**
     * Get product list
     * @return List product dto
     */
    List<ProductDto> getProducts();

    /**
     * Get products by size
     * @param size -> int
     * @return List product dto
     */
    List<ProductDto> getProductBySize(int size);

    /**
     * Get products by category
     * @param category -> string
     * @param page -> int
     * @param size -> int
     * @return List product dto
     */
    ProductPageResponse getProductsByCategory(String category, int page, int size);

    /**
     * Get products with filter applied
     * @param category -> string
     * @param page -> int
     * @param size -> int
     * @param search -> string
     * @param maxPrice -> decimal number
     * @param sortBy -> string
     * @param sortDirection -> ASC || DESC
     * @return List product dto
     */
    ProductPageResponse searchProducts(String category, int page, int size, String search, BigDecimal maxPrice, String sortBy, String sortDirection);

    /**
     * Get product by id
     * @param id -> long productId
     * @return Product dto
     */
    ProductDto getProductById(long id);

    /**
     * Get related product to the current one
     * @param category -> string
     * @param size -> int
     * @param excludeId -> long
     * @return List product dto
     */
    List<ProductDto> getRelatedProducts(String category, int size, long excludeId );
}
