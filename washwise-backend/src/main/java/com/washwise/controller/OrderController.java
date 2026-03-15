package com.washwise.controller;

import com.washwise.dto.request.CreateOrderRequest;
import com.washwise.dto.request.UpdateOrderRequest;
import com.washwise.dto.response.ApiResponse;
import com.washwise.dto.response.OrderResponse;
import com.washwise.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;

    /**
     * Get all orders (ADMIN only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders()  {
        log.info("GET /orders/admin/all - Fetch all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(
            ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .data(orders)
                .message("All orders retrieved succcessfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * Get current user's orders
     */
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUsersOrders() {
        log.info("GET /orders/my-orders - Fetch user's orders");
        List<OrderResponse> orders = orderService.getUserOrders();
        return ResponseEntity.ok(
            ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .data(orders)
                .message("User orders retrieved successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * Get order by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus (@PathVariable String status) {
        log.info("GET /orders/status/{} - Fetch orders by status", status);
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(
            ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .data(orders)
                .message("Orders retrieved by status successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );  
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
        log.info("GET /orders/{} - Fetch order by ID", id);
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(order)
                .message("Order retrieved successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * Create new order
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /orders - Create new order");
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(order)
                .message("Order created successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * UPDATE order (ADMIN only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(@PathVariable UUID id, @Valid @RequestBody UpdateOrderRequest request) {
        log.info("PUT /orders{} - Update  order", id);
        OrderResponse order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(order)
                .message("Order updated successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * Cancel order
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable UUID id) {
        log.info("PUT /orders/{}/cancel - Cancel order", id);
        OrderResponse order = orderService.cancelOrder(id);
        return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
                .success(true)
                .data(order)
                .message("Order cancelled successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * DELETE order (ADMIN only)
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
        log.info("DELETE /orders/{} - Delete order", id);
        orderService.deleteOrder(id);
        return ResponseEntity.ok(
          ApiResponse.<Void>builder()
                .success(true)
                .data(null)
                .message("Order deleted successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()  
        );
    }
}
