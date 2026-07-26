package com.movielibrary.exception;

/**
 * Thrown when a login attempt fails due to an invalid username or password
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * @param message detail message describing why authentication failed
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
