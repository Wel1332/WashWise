package com.washwise.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderRequest {

    @Size(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;

    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;
}

