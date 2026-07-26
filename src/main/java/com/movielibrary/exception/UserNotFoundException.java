package com.movielibrary.exception;

/**
 * Thrown when a user can't be  found by its id
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * @param id id of the user that couldn't be found
     */
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
