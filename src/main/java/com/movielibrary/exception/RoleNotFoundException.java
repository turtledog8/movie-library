package com.movielibrary.exception;

/**
 * Thrown when a role can't be found by its id
 */
public class RoleNotFoundException extends RuntimeException {

    /**
     * @param id id of the role that couldn't be found
     */
    public RoleNotFoundException(Long id) {
        super("Role not found: " + id);
    }
}
