package com.washwise.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    
    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;

    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date must be in the future")
    private LocalDate scheduledDate;
}
