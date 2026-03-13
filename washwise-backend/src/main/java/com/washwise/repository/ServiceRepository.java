package com.washwise.repository;

import com.washwise.entity.ServiceEntity;
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

    // Custom derived queries required by your ServiceService
    List<ServiceEntity> findByIsActiveTrueOrderByCreatedAtDesc();

    Page<ServiceEntity> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    List<ServiceEntity> findByCategoryIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(String category);

    boolean existsByNameIgnoreCase(String name);

    // Custom JPQL query for the search functionality
    @Query("SELECT s FROM ServiceEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ServiceEntity> searchServices(@Param("keyword") String keyword);
}