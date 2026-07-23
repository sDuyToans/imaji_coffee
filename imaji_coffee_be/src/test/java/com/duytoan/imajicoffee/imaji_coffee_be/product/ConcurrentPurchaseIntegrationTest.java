package com.duytoan.imajicoffee.imaji_coffee_be.product;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderItemDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.OrderStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test to simulate concurrent purchases of a product to ensure that the system does not oversell.
 * This test creates a product with limited stock and simulates multiple threads attempting to purchase it simultaneously.
 * It verifies that the total quantity sold does not exceed the available stock and that no negative quantities occur.
 * Run with mvn -Dtest=ConcurrentPurchaseIntegrationTest test
 */

@SpringBootTest
class ConcurrentPurchaseIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemServiceImpl orderItemService;

    @Autowired
    PlatformTransactionManager txMAnager;

    @Test
    void concurrentPurchases_shouldNotOverSellProduct() throws InterruptedException {
        // Arrange: create a product with limited quantity
        Product p = new Product();
        p.setName("Test Coffee");
        p.setPrice(new BigDecimal("10.00"));
        p.setCategory("coffee_baverage");
        p.setQuantity(30);
        p.setCreatedBy("test");
        productRepository.saveAndFlush(p);

        // create a persisted order to attach items to
        Order order = new Order();
        order.setEmail("test@example.com");
        order.setShippingAddress("{}");
        order.setTotalAmount(new BigDecimal("0.00"));
        order.setCurrency("USD");
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedBy("test");
        order = orderRepository.saveAndFlush(order);

        int threads = 40; //simulate 40 concurrent buyers each wanting 1 items
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger successes = new AtomicInteger(0);

        TransactionTemplate txTemplate = new TransactionTemplate(txMAnager);

        for (int i = 0; i < threads; i++) {
            Order finalOrder = order;
            exec.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    // each attempt runs in its own transaction so locks apply independently
                    txTemplate.execute(status -> {
                        try {
                            orderItemService.saveOrderItems(finalOrder, List.of(new OrderItemDto(p.getProductId(), 1)));
                            successes.incrementAndGet();
                        } catch (Exception e) {
                            // expected for failed attempts (insufficient stock or lock timeouts)
                        }
                        return null;
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        // start all threads
        ready.await();
        start.countDown();
        done.await();
        exec.shutdownNow();

        // Assert: no negative quantity and total sold <= initial stock
        Product updated = productRepository.findById(p.getProductId()).orElseThrow();
        int remaining = updated.getQuantity();
        int sold = 30 - remaining;
        assertTrue(remaining >= 0, "Remaining quantity should not be negative");
        assertTrue(sold <= 30, "Total sold should not exceed initial stock");
        assertEquals(sold, successes.get(), "success count should match actual sold quantity");
    }
}
