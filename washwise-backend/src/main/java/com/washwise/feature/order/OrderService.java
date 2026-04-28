package com.washwise.feature.order;

import com.washwise.feature.order.dto.CreateOrderRequest;
import com.washwise.feature.order.dto.UpdateOrderRequest;
import com.washwise.feature.order.dto.OrderResponse;
import com.washwise.feature.order.entity.Order;
import com.washwise.feature.order.repository.OrderRepository;
import com.washwise.feature.service.entity.ServiceEntity;
import com.washwise.feature.service.repository.ServiceRepository;
import com.washwise.feature.user.entity.User;
import com.washwise.feature.user.entity.UserRole;
import com.washwise.feature.user.repository.UserRepository;
import com.washwise.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final Pattern WEIGHT_PATTERN = Pattern.compile("Weight:\\s*(\\d+\\.?\\d*)\\s*kg");

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        LocalDateTime scheduledDateTime = parseScheduledDateTime(
                request.getScheduledDate(),
                request.getPickupTimeSlot()
        );

        Order order = Order.builder()
                .user(user)
                .service(service)
                .totalPrice(request.getTotalPrice())
                .location(request.getLocation())
                .scheduledDate(scheduledDateTime)
                .notes(buildNotes(request))
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order {} created by user {}", saved.getId(), email);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrder(String orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(parseUuid(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            if ("COMPLETED".equalsIgnoreCase(request.getStatus())) {
                order.setCompletedDate(LocalDateTime.now());
            }
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        if (request.getLocation() != null) {
            order.setLocation(request.getLocation());
        }

        Order updated = orderRepository.save(order);
        log.info("Order {} updated", updated.getId());
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteOrder(String orderId, String email) {
        Order order = orderRepository.findById(parseUuid(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = order.getUser().getId().equals(user.getId());
        if (!isOwner && !user.hasRole(UserRole.ADMIN)) {
            throw new AccessDeniedException("Not authorized to delete this order");
        }

        orderRepository.delete(order);
        log.info("Order {} deleted by {}", order.getId(), email);
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new ResourceNotFoundException("Invalid order id: " + value);
        }
    }

    private LocalDateTime parseScheduledDateTime(LocalDate date, String timeSlot) {
        LocalTime time = LocalTime.of(9, 0);
        if (timeSlot != null && !timeSlot.isBlank()) {
            try {
                int hour = Integer.parseInt(timeSlot.split("-")[0].trim());
                if (hour >= 0 && hour <= 23) {
                    time = LocalTime.of(hour, 0);
                }
            } catch (NumberFormatException ignored) {
                // fall through to default 9 AM
            }
        }
        return LocalDateTime.of(date, time);
    }

    private String buildNotes(CreateOrderRequest request) {
        StringBuilder notes = new StringBuilder();
        if (request.getWeightKg() != null && request.getWeightKg() > 0) {
            notes.append("Weight: ").append(request.getWeightKg()).append(" kg\n");
        }
        if (isPresent(request.getPickupTimeSlot())) {
            notes.append("Pickup Time Slot: ").append(request.getPickupTimeSlot()).append("\n");
        }
        if (isPresent(request.getDeliveryDate())) {
            notes.append("Delivery Date: ").append(request.getDeliveryDate()).append("\n");
        }
        if (isPresent(request.getDeliveryTimeSlot())) {
            notes.append("Delivery Time Slot: ").append(request.getDeliveryTimeSlot()).append("\n");
        }
        if (isPresent(request.getSpecialInstructions())) {
            notes.append("\nSpecial Instructions:\n").append(request.getSpecialInstructions());
        }
        String result = notes.toString().trim();
        return result.isEmpty() ? null : result;
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private Double extractWeightFromNotes(String notes) {
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }
        Matcher matcher = WEIGHT_PATTERN.matcher(notes);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException ex) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private OrderResponse mapToResponse(Order order) {
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
                .weightKg(extractWeightFromNotes(order.getNotes()))
                .build();
    }
}
