package com.duytoan.imajicoffee.imaji_coffee_be.entities.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PromoProductId implements Serializable {
    private static final long serialVersionUID = -3295258927990340386L;
    @Column(name = "promo_id", nullable = false)
    private Long promoId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PromoProductId entity = (PromoProductId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.promoId, entity.promoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, promoId);
    }

}
