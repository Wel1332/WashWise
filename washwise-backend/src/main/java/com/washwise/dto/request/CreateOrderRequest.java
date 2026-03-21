package com.washwise.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    
    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;

    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date must be in the future")
    private LocalDate scheduledDate;

    @Size(max = 20, message = "Time slot must be less than 20 characters")
    private String pickupTimeSlot;

    private String deliveryDate;
    
    private String deliveryTimeSlot;

    @Positive(message = "Weight must be positive")
    private Double weightKg;

    @Size(max = 500, message = "Special instructions must be less than 500 characters")
    private String specialInstructions;

    @Size(max = 50, message = "Status must be less than 50 characters")
    private String status;

    // Computed field - pickup address (maps to location)
    public void setPickupAddress(String pickupAddress) {
        this.location = pickupAddress;
    }

    // Computed field - pickup date (maps to scheduledDate)
    public void setPickupDate(String pickupDate) {
        if (pickupDate != null && !pickupDate.isEmpty()) {
            this.scheduledDate = LocalDate.parse(pickupDate);
        }
    }
}