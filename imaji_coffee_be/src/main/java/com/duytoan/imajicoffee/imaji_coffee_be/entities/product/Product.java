package com.duytoan.imajicoffee.imaji_coffee_be.entities.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "old_price", precision = 10, scale = 2)
    private BigDecimal oldPrice;

    @ColumnDefault("1")
    @Column(name = "is_available_at_web")
    private Boolean isAvailableAtWeb;

    @ColumnDefault("30")
    @Column(name = "quantity")
    private Integer quantity;

    @ColumnDefault("'coffee_baverage'")
    @Lob
    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "product")
    @OrderBy("isMain DESC, productImageId ASC")
    private Set<ProductImage> productImages = new LinkedHashSet<>();

    /**
     * get the main image of the product
     * @return -> string path to the image
     */
    public String getMainImageUrl() {
        return productImages.stream()
                .filter(ProductImage::getIsMain)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }
}
