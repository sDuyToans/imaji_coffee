package com.duytoan.imajicoffee.imaji_coffee_be.services.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderItemDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.Order;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.order.OrderItem;

import java.util.List;

/**
 * OrderItem interface contains method's name and parameters
 * @author duytoan
 * @since 10/2025
 */
public interface IOrderItemService {
    /**
     * Save order items
     * @param order -> order object
     * @param orderItemDtoList -> list of order items
     */
    List<OrderItem> saveOrderItems(Order order, List<OrderItemDto> orderItemDtoList);
}
