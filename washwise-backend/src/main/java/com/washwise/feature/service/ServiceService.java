package com.washwise.feature.service;

import com.washwise.feature.service.dto.CreateServiceRequest;
import com.washwise.feature.service.dto.UpdateServiceRequest;
import com.washwise.shared.dto.PageResponse;
import com.washwise.feature.service.dto.ServiceResponse;
import com.washwise.feature.service.entity.ServiceEntity;
import com.washwise.shared.exception.DuplicateResourceException;
import com.washwise.shared.exception.ResourceNotFoundException;
import com.washwise.feature.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> getAllServices() {
        log.debug("Fetching all services");
        return serviceRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "services")
    public List<ServiceResponse> getActiveServices() {
        log.debug("Fetching all active services");
        return serviceRepository.findByIsActiveTrueOrderByCreatedAtDesc()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public ServiceResponse getServiceById(UUID id) {
        log.debug("Fetching service by ID: {}", id);
        ServiceEntity service = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        return mapToResponse(service);
    }

    public List<ServiceResponse> getServicesByCategory(String category) {
        log.debug("Fetching services by category: {}", category);
        return serviceRepository.findByCategoryIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(category)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ServiceResponse> searchServices(String keyword) {
        log.debug("Searching services with keyword: {}", keyword);
        return serviceRepository.searchServices(keyword)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public ServiceResponse createService(CreateServiceRequest request) {
        log.info("Creating new service: {}", request.getName());

        if (serviceRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Service with name '" + request.getName() + "' already exists");
        }

        ServiceEntity service = ServiceEntity.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .category(request.getCategory())
            .duration(request.getDuration())
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .build();

        ServiceEntity savedService = serviceRepository.save(service);
        log.info("Service created successfully with ID: {}", savedService.getId());
        return mapToResponse(savedService);
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public ServiceResponse updateService(UUID id, UpdateServiceRequest request) {
        log.info("Updating service with ID: {}", id);

        ServiceEntity service = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));

        if (request.getName() != null && !request.getName().equalsIgnoreCase(service.getName())) {
            if (serviceRepository.existsByNameIgnoreCase(request.getName())) {
                throw new DuplicateResourceException("Service with name '" + request.getName() + "' already exists");
            }
            service.setName(request.getName());
        }

        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            service.setPrice(request.getPrice());
        }
        if (request.getCategory() != null) {
            service.setCategory(request.getCategory());
        }
        if (request.getDuration() != null) {
            service.setDuration(request.getDuration());
        }
        if (request.getIsActive() != null) {
            service.setIsActive(request.getIsActive());
        }

        ServiceEntity updatedService = serviceRepository.save(service);
        log.info("Service updated successfully with ID: {}", id);
        return mapToResponse(updatedService);
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public void deleteService(UUID id) {
        log.info("Deleting service with ID: {}", id);

        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found with ID: " + id);
        }

        serviceRepository.deleteById(id);
        log.info("Service deleted successfully with ID: {}", id);
    }

    @Cacheable(value = "services", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public PageResponse<ServiceResponse> getActiveServices(Pageable pageable) {
        log.debug("Fetching active services with pagination");
        Page<ServiceEntity> page = serviceRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        
        return PageResponse.<ServiceResponse>builder()
            .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isLast(page.isLast())
            .build();
    }

    private ServiceResponse mapToResponse(ServiceEntity service) {
        return ServiceResponse.builder()
            .id(service.getId())
            .name(service.getName())
            .description(service.getDescription())
            .price(service.getPrice())
            .category(service.getCategory())
            .duration(service.getDuration())
            .isActive(service.getIsActive())
            .createdAt(service.getCreatedAt())
            .updatedAt(service.getUpdatedAt())
            .build();
    }
}