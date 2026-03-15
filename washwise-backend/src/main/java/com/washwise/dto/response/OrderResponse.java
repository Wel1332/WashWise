package com.washwise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private UUID id;
    private UUID userId;
    private UUID serviceId;
    private String serviceName;
    private String status;
    private BigDecimal totalPrice;
    private String notes;
    private String location;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}   
