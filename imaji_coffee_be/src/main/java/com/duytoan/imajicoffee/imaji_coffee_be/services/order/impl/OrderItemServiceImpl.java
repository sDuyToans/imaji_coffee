package com.duytoan.imajicoffee.imaji_coffee_be.services.order.impl;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderItemDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Product;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.ProductImage;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.order.OrderItemRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.product.ProductRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.IOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implemented IOrderItemService Interface -> Override and implement interface's methods
 * @author duytoan
 * @since 10/2025
 */
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements IOrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    /**
     * Save order items list
     * @param order -> order object
     * @param orderItemDtoList -> list of order items
     */
    @Override
    @Transactional
    public List<OrderItem> saveOrderItems(Order order, List<OrderItemDto> orderItemDtoList) {
        List<OrderItem> orderItems = orderItemDtoList.stream().map(itemDto -> {
            Product product = productRepository.findByProductIdForUpdate(itemDto.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", itemDto.productId().toString()));

            if (Boolean.FALSE.equals(product.getIsAvailableAtWeb())) {
                throw new IllegalArgumentException("Product is unavailable: " + product.getName());
            }

            if (itemDto.quantity() == null || itemDto.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            // Deduct product quantity
            int newQuantity = product.getQuantity() - itemDto.quantity();
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }
            product.setQuantity(newQuantity);
            productRepository.save(product);

            //create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getProductId());
            orderItem.setProductName(product.getName());
            String productImg = product.getProductImages()
                    .stream()
                    .filter(ProductImage::getIsMain)
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(null);
            orderItem.setProductImg(productImg);
            orderItem.setProductCategory(product.getCategory());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setCreatedBy("ADMIN");
            return orderItem;
        }).toList();

        return orderItemRepository.saveAll(orderItems);
    }

    @Transactional
    public void restoreProductStock(Long productId, int quantity) {
        Product product = productRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId.toString()));
        product.setQuantity(product.getQuantity() + quantity);
        productRepository.save(product);
    }
}
