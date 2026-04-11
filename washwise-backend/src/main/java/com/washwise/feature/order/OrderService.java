package com.washwise.feature.order;

import com.washwise.feature.order.dto.CreateOrderRequest;
import com.washwise.feature.order.dto.UpdateOrderRequest;
import com.washwise.feature.order.dto.OrderResponse;
import com.washwise.feature.order.entity.Order;
import com.washwise.feature.service.entity.ServiceEntity;
import com.washwise.feature.user.entity.UserRole;
import com.washwise.feature.user.entity.User;
import com.washwise.feature.order.repository.OrderRepository;
import com.washwise.feature.service.repository.ServiceRepository;
import com.washwise.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public void deleteOrder(String orderId, String email) {
        UUID id = UUID.fromString(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Security check: Only allow deletion if the user owns the order OR is an ADMIN
        if (!order.getUser().getId().equals(user.getId()) && !user.hasRole(UserRole.ADMIN)) {
            throw new RuntimeException("Not authorized to delete this order");
        }

        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        // Get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get service
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Parse scheduled date and time
        LocalDateTime scheduledDateTime = parseScheduledDateTime(
            request.getScheduledDate(), 
            request.getPickupTimeSlot()
        );

        // Build notes from all fields
        String notes = buildNotes(request);

        // Create order
        Order order = Order.builder()
                .user(user)
                .service(service)
                .totalPrice(request.getTotalPrice())
                .location(request.getLocation())
                .scheduledDate(scheduledDateTime)
                .notes(notes)
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .build();

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrder(String orderId, UpdateOrderRequest request) {
        UUID id = UUID.fromString(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update fields if provided
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            
            // If status is COMPLETED, set completed_date
            if ("COMPLETED".equals(request.getStatus())) {
                order.setCompletedDate(LocalDateTime.now());
            }
        }

        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }

        if (request.getLocation() != null) {
            order.setLocation(request.getLocation());
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    private LocalDateTime parseScheduledDateTime(LocalDate date, String timeSlot) {
        LocalTime time;
        
        try {
            if (timeSlot != null && !timeSlot.isEmpty()) {
                // Parse time slot (format: "8-10" means 8:00 AM)
                String[] times = timeSlot.split("-");
                int hour = Integer.parseInt(times[0]);
                time = LocalTime.of(hour, 0);
            } else {
                time = LocalTime.of(9, 0); // Default to 9 AM
            }
        } catch (Exception e) {
            time = LocalTime.of(9, 0); // Default to 9 AM on error
        }
        
        return LocalDateTime.of(date, time);
    }

    private String buildNotes(CreateOrderRequest request) {
        StringBuilder notes = new StringBuilder();
        
        if (request.getWeightKg() != null && request.getWeightKg() > 0) {
            notes.append("Weight: ").append(request.getWeightKg()).append(" kg\n");
        }
        
        if (request.getPickupTimeSlot() != null && !request.getPickupTimeSlot().isEmpty()) {
            notes.append("Pickup Time Slot: ").append(request.getPickupTimeSlot()).append("\n");
        }
        
        if (request.getDeliveryDate() != null && !request.getDeliveryDate().isEmpty()) {
            notes.append("Delivery Date: ").append(request.getDeliveryDate()).append("\n");
        }
        
        if (request.getDeliveryTimeSlot() != null && !request.getDeliveryTimeSlot().isEmpty()) {
            notes.append("Delivery Time Slot: ").append(request.getDeliveryTimeSlot()).append("\n");
        }
        
        if (request.getSpecialInstructions() != null && !request.getSpecialInstructions().isEmpty()) {
            notes.append("\nSpecial Instructions:\n").append(request.getSpecialInstructions());
        }
        
        String result = notes.toString().trim();
        return result.isEmpty() ? null : result;
    }

    private Double extractWeightFromNotes(String notes) {
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }
        
        // Try to extract weight from notes (format: "Weight: 5.0 kg")
        Pattern pattern = Pattern.compile("Weight:\\s*(\\d+\\.?\\d*)\\s*kg");
        Matcher matcher = pattern.matcher(notes);
        
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        
        return 0.0;
    }

    private OrderResponse mapToResponse(Order order) {
        // Extract weight from notes for frontend display
        Double weightKg = extractWeightFromNotes(order.getNotes());
        
        return OrderResponse.builder()
                .id(order.getId().toString())
                .user(OrderResponse.UserBasicInfo.builder()
                        .id(order.getUser().getId().toString())
                        .fullName(order.getUser().getFullName())
                        .email(order.getUser().getEmail())
                        .build())
                .service(OrderResponse.ServiceBasicInfo.builder()
                        .id(order.getService().getId().toString())
                        .name(order.getService().getName())
                        .description(order.getService().getDescription())
                        .category(order.getService().getCategory())
                        .price(order.getService().getPrice())
                        .duration(order.getService().getDuration())
                        .imageUrl(order.getService().getImageUrl())
                        .build())
                .totalPrice(order.getTotalPrice())
                .location(order.getLocation())
                .scheduledDate(order.getScheduledDate())
                .completedDate(order.getCompletedDate())
                .notes(order.getNotes())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .weightKg(weightKg)
                .build();
    }
}