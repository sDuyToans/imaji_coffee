package com.duytoan.imajicoffee.imaji_coffee_be.repository.product;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.product.Ship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Ship repository
 * @author duytoan
 * @since 10/2025
 */
public interface ShipRepository extends JpaRepository<Ship, Long> {
    /**
     * Get active ship method
     * @return ship method list
     */
    List<Ship> findAllByIsActiveTrue();
}
