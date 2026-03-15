package com.washwise.service;

import com.washwise.dto.request.CreateOrderRequest;
import com.washwise.dto.request.UpdateOrderRequest;
import com.washwise.dto.response.OrderResponse;
import com.washwise.entity.Order;
import com.washwise.entity.ServiceEntity;
import com.washwise.entity.User;
import com.washwise.exception.ResourceNotFoundException;
import com.washwise.repository.OrderRepository;
import com.washwise.repository.ServiceRepository;
import com.washwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    /**
     * Get all orders (admin only)
     */
    public List<OrderResponse> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get current user's orders
     */
    public List<OrderResponse> getUserOrders() {
        User user = getCurrentUser();
        log.debug("Fetching orders for user: {}", user.getId());
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get orders by status
     */
    public List<OrderResponse> getOrdersByStatus(String status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     */
    public OrderResponse getOrderById(UUID id) {
        log.debug("Fetching order by ID: {}", id);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        
        // Check if user can access this order
        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId()) && !isAdmin()) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }
        
        return mapToResponse(order);
    }

    /**
     * Create new order
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating new order for service: {}", request.getServiceId());

        User user = getCurrentUser();
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));

        Order order = Order.builder()
            .user(user)
            .service(service)
            .status("PENDING")
            .totalPrice(service.getPrice())
            .notes(request.getNotes())
            .location(request.getLocation())
            .scheduledDate(request.getScheduledDate().atStartOfDay())
            .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return mapToResponse(savedOrder);
    }
     /**
     * Update order (admin only)
     */
     public OrderResponse updateOrder(UUID id, UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", id);

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        if (request.getLocation() != null) {
            order.setLocation(request.getLocation());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully with ID: {}", id);
        return mapToResponse(updatedOrder);
    }

    /**
     * Cancel order
     */
    public OrderResponse cancelOrder(UUID id) {
        log.info("Cancelling order with ID: {}", id);

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId()) && !isAdmin()) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }

        order.setStatus("CANCELLED");
        Order updatedOrder = orderRepository.save(order);
        log.info("Order cancelled successfully with ID: {}", id);
        return mapToResponse(updatedOrder);
    }

    /**
     * Delete order (admin only)
     */
    public void deleteOrder(UUID id) {
        log.info("Deleting order with ID: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Map Order entity to OrderResponse DTO
     */
    private OrderResponse mapToResponse(Order order) {
    return OrderResponse.builder()
        .id(order.getId())
        .userId(order.getUser().getId())
        .serviceId(order.getService().getId())
        .serviceName(order.getService().getName())
        .status(order.getStatus())
        .totalPrice(order.getTotalPrice())
        .notes(order.getNotes())
        .location(order.getLocation())
        .scheduledDate(order.getScheduledDate())
        .completedDate(order.getCompletedDate())
        .createdAt(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now())
        .updatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt() : LocalDateTime.now())
        .build();
}
}
