package com.duytoan.imajicoffee.imaji_coffee_be.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderItemDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderItemRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Test
    void saveOrderItems_throwsWhenStockIsInsufficient() {
        Product product = new Product();
        product.setProductId(1L);
        product.setName("Coffee");
        product.setPrice(new BigDecimal("5.00"));
        product.setQuantity(1);
        product.setIsAvailableAtWeb(true);

        when(productRepository.findByProductIdForUpdate(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderItemService.saveOrderItems(new Order(), List.of(new OrderItemDto(1L, 2))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not enough stock");
    }
}
