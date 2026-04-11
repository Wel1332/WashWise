package com.washwise.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper
 * Wraps all API responses for consistency
 * @param <T> type of data in response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates if request was successful
     */
    private boolean success;

    /**
     * Response data
     * Null if request failed
     */
    private T data;

    /**
     * Human-readable message
     * Success message or error description
     */
    private String message;

    /**
     * Error details if request failed
     * Null on success
     */
    private Object error;

    /**
     * Response timestamp
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     */
    private int statusCode;

    /**
     * Create a success response
     */
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message, Object error, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}