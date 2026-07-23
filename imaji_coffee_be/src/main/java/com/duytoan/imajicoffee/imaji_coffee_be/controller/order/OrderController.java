package com.duytoan.imajicoffee.imaji_coffee_be.controller.order;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderDetailResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.OrderResponseDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.order.UpdateOrderStatusDto;
import com.duytoan.imajicoffee.imaji_coffee_be.services.order.IOrderService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author duytoan
 * @since 10/2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final IOrderService orderService;

    /**
     * Create order
     * @param request -> order request dto
     * @return -> res entity
     * @throws MessagingException -> mess ex
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto request,
            Authentication authentication
    ) throws MessagingException {
        Long userId = currentUserId(authentication);
        OrderResponseDto orderResponseDto = orderService.createOrder(request, userId);
        return ResponseEntity.ok(orderResponseDto);
    }

    /**
     * Create order for PayPal
     * @param request -> order request dto
     * @return -> res entity
     * @throws MessagingException -> mess ex
     */
    @PostMapping("/paypal")
    public ResponseEntity<OrderResponseDto> createOrderForPayPal(
            @Valid @RequestBody OrderRequestDto request,
            Authentication authentication
    ) throws MessagingException {
        Long userId = currentUserId(authentication);
        OrderResponseDto orderResponseDto = orderService.createOrderForPaypal(request, userId);
        return ResponseEntity.ok(orderResponseDto);
    }

    /**
     * Get order by order id
     * @param orderId -> long order id
     * @return -> res entity {order}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrderById(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrder(orderId, currentUserId(authentication), hasAdminRole(authentication)));
    }

    /**
     * Update order status
     * @param orderId -> long order id
     * @param updateOrderStatusDto -> update order status dto
     * @return -> res entity
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusDto updateOrderStatusDto,
            Authentication authentication
    ) {
        OrderResponseDto orderResponseDto = orderService.updateOrderStatusByAdmin(
                orderId,
                updateOrderStatusDto.status(),
                authentication.getName()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderResponseDto);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        OrderResponseDto orderResponseDto = orderService.cancelOrder(
                orderId,
                currentUserId(authentication),
                hasAdminRole(authentication),
                authentication.getName()
        );
        return ResponseEntity.ok(orderResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/collect-cod-payment")
    public ResponseEntity<OrderResponseDto> collectCodPayment(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        OrderResponseDto orderResponseDto = orderService.recordCodPaymentCollection(
                orderId,
                authentication.getName()
        );
        return ResponseEntity.ok(orderResponseDto);
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof String principalValue) {
            try {
                return Long.parseLong(principalValue);
            } catch (NumberFormatException ignored) {
                // fallback below
            }
        }
        return Long.parseLong(authentication.getName());
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
