package com.washwise.feature.service;

import com.washwise.feature.service.dto.CreateServiceRequest;
import com.washwise.feature.service.dto.UpdateServiceRequest;
import com.washwise.shared.dto.ApiResponse;
import com.washwise.shared.dto.PageResponse;
import com.washwise.feature.service.dto.ServiceResponse;
import com.washwise.feature.service.ServiceService;

import com.washwise.feature.service.entity.ServiceEntity; 
import com.washwise.feature.service.repository.ServiceRepository;
import com.washwise.shared.service.ImageService;
import java.util.Map; 

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Services", description = "Service management endpoints")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;
    
    // ADDED DEPENDENCIES FOR IMAGE UPLOAD
    private final ImageService imageService;
    private final ServiceRepository serviceRepository;

    /**
     * GET all services with pagination (public)
     */
    @GetMapping
    @Operation(summary = "Get all services with pagination", description = "Retrieve all services with pagination support")
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>> getServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /services - Fetch services with pagination (page={}, size={})", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<ServiceResponse> response = serviceService.getActiveServices(pageable);
        
        return ResponseEntity.ok(
            ApiResponse.<PageResponse<ServiceResponse>>builder()
                .success(true)
                .data(response)
                .message("Services retrieved successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * GET active services without pagination (public)
     */
    @GetMapping("/active")
    @Operation(summary = "Get all active services", description = "Retrieve all active services")
    public ResponseEntity<ApiResponse<java.util.List<ServiceResponse>>> getActiveServices() {
        log.info("GET /services/active - Fetch all active services");
        java.util.List<ServiceResponse> services = serviceService.getActiveServices();
        return ResponseEntity.ok(
            ApiResponse.<java.util.List<ServiceResponse>>builder()
                .success(true)
                .data(services)
                .message("Active services retrieved successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * GET service by ID (public)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID", description = "Retrieve a specific service by its ID")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable UUID id) {
        log.info("GET /services/{} - Fetch service by ID", id);
        ServiceResponse service = serviceService.getServiceById(id);
        return ResponseEntity.ok(
            ApiResponse.<ServiceResponse>builder()
                .success(true)
                .data(service)
                .message("Service retrieved successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * GET services by category (public)
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get services by category", description = "Retrieve all services in a specific category")
    public ResponseEntity<ApiResponse<java.util.List<ServiceResponse>>> getServicesByCategory(
            @PathVariable String category) {
        log.info("GET /services/category/{} - Fetch services by category", category);
        java.util.List<ServiceResponse> services = serviceService.getServicesByCategory(category);
        return ResponseEntity.ok(
            ApiResponse.<java.util.List<ServiceResponse>>builder()
                .success(true)
                .data(services)
                .message("Services retrieved by category successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * SEARCH services (public)
     */
    @GetMapping("/search/{keyword}")
    @Operation(summary = "Search services", description = "Search services by keyword in name or description")
    public ResponseEntity<ApiResponse<java.util.List<ServiceResponse>>> searchServices(
            @PathVariable String keyword) {
        log.info("GET /services/search/{} - Search services", keyword);
        java.util.List<ServiceResponse> services = serviceService.searchServices(keyword);
        return ResponseEntity.ok(
            ApiResponse.<java.util.List<ServiceResponse>>builder()
                .success(true)
                .data(services)
                .message("Services found successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    /**
     * CREATE new service (ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new service", description = "Create a new service (admin only)")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(
            @Valid @RequestBody CreateServiceRequest request) {
        log.info("POST /services - Create new service: {}", request.getName());
        ServiceResponse service = serviceService.createService(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<ServiceResponse>builder()
                .success(true)
                .data(service)
                .message("Service created successfully")
                .statusCode(201)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * UPDATE service (ADMIN only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update service", description = "Update an existing service (admin only)")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceRequest request) {
        log.info("PUT /services/{} - Update service", id);
        ServiceResponse service = serviceService.updateService(id, request);
        return ResponseEntity.ok(
            ApiResponse.<ServiceResponse>builder()
                .success(true)
                .data(service)
                .message("Service updated successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * DELETE service (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete service", description = "Delete a service (admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable UUID id) {
        log.info("DELETE /services/{} - Delete service", id);
        serviceService.deleteService(id);
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .success(true)
                .data(null)
                .message("Service deleted successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/{id}/upload-image")
    @Operation(summary = "Upload image", description = "Upload an image for a service")
    public ResponseEntity<?> uploadServiceImage(
            @PathVariable UUID id, // Changed to UUID to match your other endpoints
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageService.uploadImage(file);
            ServiceEntity service = serviceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            
            service.setImageUrl(imageUrl);
            serviceRepository.save(service);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}