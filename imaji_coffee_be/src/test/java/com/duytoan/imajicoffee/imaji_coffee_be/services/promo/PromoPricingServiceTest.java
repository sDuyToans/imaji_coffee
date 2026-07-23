package com.duytoan.imajicoffee.imaji_coffee_be.services.promo;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoProduct;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.PromoProductId;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromoPricingServiceTest {

    @Mock
    private PromoUsageRepository promoUsageRepository;

    @Test
    void evaluate_rejectsExpiredPromo() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("fixed", new BigDecimal("5.00"));
        promo.setEndAt(Instant.now().minusSeconds(60));

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("2.00"), true
        );

        assertThat(result.accepted()).isFalse();
        assertThat(result.message()).contains("expired");
    }

    @Test
    void evaluate_rejectsUpcomingPromo() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("fixed", new BigDecimal("5.00"));
        promo.setStartAt(Instant.now().plusSeconds(3600));

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("2.00"), true
        );

        assertThat(result.accepted()).isFalse();
        assertThat(result.message()).contains("not available yet");
    }

    @Test
    void evaluate_rejectsMinimumOrderNotReached() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("fixed", new BigDecimal("5.00"));
        promo.setMinimumOrderAmount(new BigDecimal("30.00"));

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("2.00"), true
        );

        assertThat(result.accepted()).isFalse();
        assertThat(result.eligibilityHint()).isEqualTo("min-order");
    }

    @Test
    void evaluate_rejectsPerUserLimit() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("fixed", new BigDecimal("5.00"));
        promo.setMaxUsesPerUser(1);
        when(promoUsageRepository.countByPromo_PromoIdAndUserId(1L, 10L)).thenReturn(1L);

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("2.00"), true
        );

        assertThat(result.accepted()).isFalse();
        assertThat(result.eligibilityHint()).isEqualTo("already-used");
    }

    @Test
    void evaluate_rejectsGlobalUsageLimit() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("fixed", new BigDecimal("5.00"));
        promo.setMaxTotalUses(2);
        promo.setUsageCount(2);

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("2.00"), true
        );

        assertThat(result.accepted()).isFalse();
        assertThat(result.eligibilityHint()).isEqualTo("usage-limit");
    }

    @Test
    void evaluate_capsFixedDiscountAtEligibleSubtotal() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("fixed", new BigDecimal("50.00"));

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 2)),
                new BigDecimal("20.00"), new BigDecimal("2.00"), true
        );

        assertThat(result.accepted()).isTrue();
        assertThat(result.discountAmount()).isEqualByComparingTo("20.00");
    }

    @Test
    void evaluate_appliesFreeShippingDiscount() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("free_shipping", BigDecimal.ZERO);

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("5.00"), true
        );

        assertThat(result.accepted()).isTrue();
        assertThat(result.discountAmount()).isEqualByComparingTo("5.00");
        assertThat(result.shippingAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void evaluate_checksEligibleProductsAndCategory() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("percentage", new BigDecimal("10.00"));
        promo.setEligibleCategory("food_snack");

        Product product = new Product();
        product.setProductId(99L);
        PromoProduct promoProduct = new PromoProduct();
        PromoProductId id = new PromoProductId();
        id.setPromoId(1L);
        id.setProductId(99L);
        promoProduct.setPromoProductId(id);
        promoProduct.setPromo(promo);
        promoProduct.setProduct(product);
        promo.setPromoProducts(Set.of(promoProduct));

        PromoPricingSnapshot fail = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("2.00"), true
        );
        assertThat(fail.accepted()).isFalse();

        PromoPricingSnapshot ok = service.evaluate(
                promo, 10L, List.of(item(99L, "food_snack", "20.00", 1)),
                new BigDecimal("20.00"), new BigDecimal("2.00"), true
        );
        assertThat(ok.accepted()).isTrue();
        assertThat(ok.discountAmount()).isEqualByComparingTo("2.00");
    }

    @Test
    void evaluate_roundsPercentageDiscountHalfUp() {
        PromoPricingService service = new PromoPricingService(promoUsageRepository);
        Promo promo = basePromo("percentage", new BigDecimal("33.3333"));

        PromoPricingSnapshot result = service.evaluate(
                promo, 10L, List.of(item(1L, "coffee_baverage", "10.00", 1)),
                new BigDecimal("10.00"), new BigDecimal("0.00"), true
        );

        assertThat(result.accepted()).isTrue();
        assertThat(result.discountAmount()).isEqualByComparingTo("3.33");
    }

    private Promo basePromo(String type, BigDecimal value) {
        Promo promo = new Promo();
        promo.setPromoId(1L);
        promo.setCode("PROMO10");
        promo.setDiscountType(type);
        promo.setDiscountValue(value);
        promo.setIsActive(true);
        promo.setStatus("ACTIVE");
        promo.setUsageCount(0);
        return promo;
    }

    private OrderItem item(Long productId, String category, String price, int quantity) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setProductCategory(category);
        item.setPrice(new BigDecimal(price));
        item.setQuantity(quantity);
        return item;
    }
}
