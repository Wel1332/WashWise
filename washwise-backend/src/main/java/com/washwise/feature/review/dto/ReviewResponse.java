package com.washwise.feature.review.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
    private String id;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse(String id, String userName, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getUserName() { return userName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}