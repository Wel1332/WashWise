package com.washwise.feature.review;

import com.washwise.feature.review.dto.ReviewRequest;
import com.washwise.feature.review.dto.ReviewResponse;
import com.washwise.feature.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = auth.getName();
            
            ReviewResponse review = reviewService.createReview(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", review);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<?> getServiceReviews(@PathVariable String serviceId) {
        try {
            List<ReviewResponse> reviews = reviewService.getServiceReviews(serviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", reviews);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/service/{serviceId}/rating")
    public ResponseEntity<?> getAverageRating(@PathVariable String serviceId) {
        try {
            Double avgRating = reviewService.getAverageRating(serviceId);
            Long reviewCount = reviewService.getReviewCount(serviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", new HashMap<String, Object>() {{
                put("averageRating", avgRating);
                put("reviewCount", reviewCount);
            }});
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}