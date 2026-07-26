package com.movielibrary.exception;

/**
 * Thrown when a movie cant be found by its id
 */
public class MovieNotFoundException extends RuntimeException {

    /**
     * @param id id of the movie that couldn't be found
     */
    public MovieNotFoundException(Long id) {
        super("Movie not found: " + id);
    }
}
