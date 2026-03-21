package com.washwise.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private String id;
    private UserBasicInfo user;
    private ServiceBasicInfo service;
    private BigDecimal totalPrice;
    private String location;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Add weightKg for frontend compatibility
    private Double weightKg;

    @Data
    @Builder
    public static class UserBasicInfo {
        private String id;
        private String fullName;
        private String email;
    }

    @Data
    @Builder
    public static class ServiceBasicInfo {
        private String id;
        private String name;
        private String description;
        private String category;
        private BigDecimal price;
        private String duration;
        private String imageUrl;
    }
}