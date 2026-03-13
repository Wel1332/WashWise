package com.washwise.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateServiceRequest {

    @NotBlank(message = "Service name is required")
    @Size(min = 3, max = 100, message = "Service name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Service description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Service price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Price must be less than 100,000")
    private BigDecimal price;

    @NotBlank(message = "Service category is required")
    @Size(min = 3, max = 50, message = "Category must be between 3 and 50 characters")
    private String category;

    @Size(max = 50, message = "Duration must be less than 50 characters")
    private String duration;

    @NotNull(message = "isActive field is required")
    private Boolean isActive;
}