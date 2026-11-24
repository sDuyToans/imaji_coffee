package com.duytoan.imajicoffee.imaji_coffee_be.services.cart;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.cart.CartDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.CartItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.cart.Cart;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Promo;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.cart.CartRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.PromoUsageRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ShipRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.cart.impl.CartServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.promo.PromoPricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock private CartRepository cartRepository;
    @Mock private ShipRepository shipRepository;
    @Mock private PromoRepository promoRepository;
    @Mock private PromoUsageRepository promoUsageRepository;

    private CartServiceImpl service;
    private Cart cart;
    private Promo promo;

    @BeforeEach
    void setUp() {
        service = new CartServiceImpl(cartRepository, shipRepository, promoRepository,
                new PromoPricingService(promoUsageRepository));
        User user = new User();
        user.setUserId(10L);
        Product product = new Product();
        product.setProductId(101L);
        product.setName("Coffee");
        product.setCategory("coffee_baverage");
        product.setPrice(new BigDecimal("10.00"));
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProduct(product);
        item.setQuantity(1);
        Ship ship = new Ship();
        ship.setPrice(new BigDecimal("5.00"));
        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setShipMethod(ship);
        cart.getCartItems().add(item);
        promo = new Promo();
        promo.setPromoId(1L);
        promo.setCode("FREE");
        promo.setIsActive(true);
        promo.setDiscountType("free_shipping");
        promo.setDiscountValue(BigDecimal.ZERO);
        when(cartRepository.findByUser_UserId(10L)).thenReturn(Optional.of(cart));
    }

    @ParameterizedTest
    @CsvSource({
            "free_shipping, 0, 5.00, 5.00, 11.00",
            "free_shipping, 0, 0.00, 0.00, 11.00",
            "free_shipping, 0, 20.00, 20.00, 11.00",
            "free_shipping, 0, 2.35, 2.35, 11.00",
            "percentage, 10, 5.00, 1.00, 15.00",
            "fixed, 3, 5.00, 3.00, 13.00",
            "none, 0, 5.00, 0.00, 16.00"
    })
    void getCart_appliesPromotionOnceInBothBreakdowns(String type, String value, String shipping,
                                                     String discount, String total) {
        cart.getShipMethod().setPrice(new BigDecimal(shipping));
        if (!"none".equals(type)) {
            promo.setDiscountType(type);
            promo.setDiscountValue(new BigDecimal(value));
            cart.setPromo(promo);
        }

        CartDto response = service.getCart(10L);

        assertThat(response.total()).isEqualByComparingTo(total);
        assertThat(response.subtotal()).isEqualByComparingTo("10.00");
        assertThat(response.tax()).isEqualByComparingTo("1.00");
        assertThat(response.shipping()).isEqualByComparingTo(shipping);
        assertThat(response.discount()).isEqualByComparingTo(discount);
        assertThat(response.total()).isEqualByComparingTo(response.subtotal().add(response.tax())
                .add(response.shipping()).subtract(response.discount()));
        assertThat(response.total().scale()).isEqualTo(2);
        if (response.promoValidation() != null) {
            assertThat(response.promoValidation().accepted()).isTrue();
            assertThat(response.promoValidation().total()).isEqualByComparingTo(total);
            assertThat(response.promoValidation().shipping()).isEqualByComparingTo(shipping);
            assertThat(response.promoValidation().discountAmount()).isEqualByComparingTo(discount);
        }
    }

    @Test
    void applyPromoCode_returnsOneShippingWaiverAfterSavingPromo() {
        when(promoRepository.findByCodeIgnoreCase("FREE")).thenReturn(Optional.of(promo));

        CartDto response = service.applyPromoCode(10L, "FREE");

        verify(cartRepository).save(cart);
        assertThat(cart.getPromo()).isSameAs(promo);
        assertThat(response.total()).isEqualByComparingTo("11.00");
        assertThat(response.shipping()).isEqualByComparingTo("5.00");
        assertThat(response.discount()).isEqualByComparingTo("5.00");
        assertThat(response.promoValidation().total()).isEqualByComparingTo("11.00");
    }
}
