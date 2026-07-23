package com.duytoan.imajicoffee.imaji_coffee_be.services.product.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductImageDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.product.ProductPageResponse;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.ProductImage;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implemented ProductService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;

    /**
     * Get product list
     * @return List product dto
     */
    @Override
    @Cacheable(value = "products")
    public List<ProductDto> getProducts() {
        return productRepository.findAll().stream().map(this::mapToProductDto).collect(Collectors.toList());
    }

    /**
     * Get products by size
     * @param size -> int
     * @return List product dto
     */
    @Override
    @Cacheable(value = "products_size")
    public List<ProductDto> getProductBySize(int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    /**
     * Get products by category
     * @param category -> string
     * @param page -> int
     * @param size -> int
     * @return List product dto
     */
    @Override
    @Cacheable("products_category")
    public ProductPageResponse getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByCategory(category, pageable);

        List<ProductDto> productDtoList = productPage.getContent().stream().map(this::mapToProductDto).toList();
        return new ProductPageResponse(productDtoList, productPage.getTotalPages(), productPage.getTotalElements());
    }

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
    @Override
//    @Cacheable("searchProducts")
    public ProductPageResponse searchProducts(String category, int page, int size, String search, BigDecimal maxPrice, String sortBy, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Product> productPage = productRepository.searchProducts(
                category,
                (search != null && !search.isEmpty()) ? search : null,
                maxPrice,
                pageable
        );
        List<ProductDto> productDtoList = productPage.getContent().stream().map(this::mapToProductDto).toList();
        return new ProductPageResponse(productDtoList, productPage.getTotalPages(), productPage.getTotalElements());
    }

    /**
     * Get product by id
     * @param id -> long productId
     * @return Product dto
     */
    @Override
    public ProductDto getProductById(long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", String.valueOf(id)));
        return mapToProductDto(product);
    }

    /**
     * Get related product to the current one
     * @param category -> string
     * @param size -> int
     * @param excludeId -> long
     * @return List product dto
     */
    @Override
    public List<ProductDto> getRelatedProducts(String category, int size, long excludeId) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
        return productRepository.getRelatedProducts(category, excludeId, pageable).stream().map(this::mapToProductDto).toList();
    }

    /**
     * Map product to product dto
     * @param product -> product object
     * @return product dto
     */
    private ProductDto mapToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto);
        if (!product.getProductImages().isEmpty()) {
            Set<ProductImageDto> productImageDtoSet = product.getProductImages().stream().map(this::mapToProductImageDto).collect(Collectors.toSet());
            productDto.setImages(productImageDtoSet);
        }
        return productDto;
    }

    /**
     * Map product image to product image dto
     * @param productImage -> product image object
     * @return product image dto
     */
    private ProductImageDto mapToProductImageDto(ProductImage productImage) {
        ProductImageDto productImageDto = new ProductImageDto();
        BeanUtils.copyProperties(productImage, productImageDto);
        return productImageDto;
    }
}
