package com.duytoan.imajicoffee.imaji_coffee_be.entities.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "promo_products")
public class PromoProduct extends BaseEntity {
    @EmbeddedId
    private PromoProductId promoProductId;

    @MapsId("promoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
