package com.washwise.feature.service.repository;

import com.washwise.feature.service.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {

    // Returns a list of all active services, newest first
    List<ServiceEntity> findByIsActiveTrueOrderByCreatedAtDesc();

    // Returns a paginated list of active services, newest first
    Page<ServiceEntity> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // Finds active services matching a specific category, newest first
    List<ServiceEntity> findByCategoryIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(String category);

    // Checks if a service with the exact name already exists (useful for validation)
    boolean existsByNameIgnoreCase(String name);

    // Custom JPQL query to search for keywords in both the name and description
    @Query("SELECT s FROM ServiceEntity s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ServiceEntity> searchServices(@Param("keyword") String keyword);
}