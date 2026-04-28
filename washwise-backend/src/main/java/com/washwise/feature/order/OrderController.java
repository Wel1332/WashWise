package com.washwise.feature.order;

import com.washwise.feature.order.dto.CreateOrderRequest;
import com.washwise.feature.order.dto.UpdateOrderRequest;
import com.washwise.feature.order.dto.OrderResponse;
import com.washwise.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management endpoints")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Authenticated customers create an order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        String email = currentEmail();
        log.info("POST /orders - {} creating order for service {}", email, request.getServiceId());
        OrderResponse order = orderService.createOrder(email, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get my orders", description = "List orders for the authenticated user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
        String email = currentEmail();
        log.debug("GET /orders/my-orders - {}", email);
        List<OrderResponse> orders = orderService.getMyOrders(email);
        return ResponseEntity.ok(
                ApiResponse.success(orders, "Orders retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all orders", description = "Admin/Staff view of every order in the system")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        log.debug("GET /orders - admin/staff listing all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(
                ApiResponse.success(orders, "Orders retrieved successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Update order", description = "Admin/Staff update an order's status or details")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderRequest request) {
        log.info("PUT /orders/{} - status={}, location={}", id, request.getStatus(), request.getLocation());
        OrderResponse order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(order, "Order updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Owner or admin can delete an order")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        String email = currentEmail();
        log.info("DELETE /orders/{} - requested by {}", id, email);
        orderService.deleteOrder(id, email);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Order deleted successfully", HttpStatus.OK.value()));
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
