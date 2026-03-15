package com.washwise.repository;

import com.washwise.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByServiceId(UUID serviceId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.service.id = ?1")
    Double getAverageRating(UUID serviceId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.service.id = ?1")
    Long getReviewCount(UUID serviceId);
}