package com.washwise.shared.exception;

/**
 * Thrown when a requested resource is not found
 * Example: user with given ID doesn't exist
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}