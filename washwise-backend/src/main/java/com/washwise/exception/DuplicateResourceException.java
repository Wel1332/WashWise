package com.washwise.exception;

/**
 * Thrown when trying to create a resource that already exists
 * Example: registering with an email that's already taken
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}