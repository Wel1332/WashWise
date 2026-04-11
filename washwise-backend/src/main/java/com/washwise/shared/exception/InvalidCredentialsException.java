package com.washwise.shared.exception;

/**
 * Thrown when login credentials are invalid
 * Email doesn't exist or password is wrong
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}