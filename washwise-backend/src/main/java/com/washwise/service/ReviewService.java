package com.washwise.service;

import com.washwise.dto.request.ReviewRequest;
import com.washwise.dto.response.ReviewResponse;
import com.washwise.entity.Review;
import com.washwise.entity.ServiceEntity;
import com.washwise.entity.User;
import com.washwise.repository.ReviewRepository;
import com.washwise.repository.ServiceRepository;
import com.washwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    public ReviewResponse createReview(ReviewRequest request, String userId) {
        // FIXED: Converted userId String to UUID for the repository
        UUID userUuid = UUID.fromString(userId);
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Convert serviceId String to UUID
        UUID serviceId = UUID.fromString(request.getServiceId());
        
        // Now serviceRepository.findById expects UUID
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Review review = new Review(user, service, request.getRating(), request.getComment());
        Review savedReview = reviewRepository.save(review);

        return new ReviewResponse(
                savedReview.getId(),
                savedReview.getUser().getFullName(),
                savedReview.getRating(),
                savedReview.getComment(),
                savedReview.getCreatedAt()
        );
    }

    public List<ReviewResponse> getServiceReviews(String serviceId) {
        UUID serviceUUID = UUID.fromString(serviceId);
        
        return reviewRepository.findByServiceId(serviceUUID)
                .stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getUser().getFullName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public Double getAverageRating(String serviceId) {
        UUID serviceUUID = UUID.fromString(serviceId);
        
        Double avgRating = reviewRepository.getAverageRating(serviceUUID);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }

    public Long getReviewCount(String serviceId) {
        UUID serviceUUID = UUID.fromString(serviceId);
        
        return reviewRepository.getReviewCount(serviceUUID);
    }
}