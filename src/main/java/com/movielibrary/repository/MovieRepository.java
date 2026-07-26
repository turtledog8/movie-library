package com.movielibrary.repository;

import com.movielibrary.model.Movie;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link Movie} entities
 */
public interface MovieRepository {

    /**
     * @return all movies in the library
     */
    List<Movie> findAll();

    /**
     * @param id id of the movie to find
     * @return the matching movie, or {@link Optional#empty()} if none exists
     */
    Optional<Movie> findById(Long id);

    /**
     * Inserts a new movie or updates an existing one, depending on whether it has an id
     *
     * @param movie the movie to persist
     * @return the persisted movie
     */
    Movie save(Movie movie);

    /**
     * @param movie the movie to remove
     */
    void delete(Movie movie);
}
