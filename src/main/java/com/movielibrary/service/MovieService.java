package com.movielibrary.service;

import com.movielibrary.dto.MovieRequestDTO;
import com.movielibrary.dto.MovieResponseDTO;

import java.util.List;

/**
 * Business operations for managing movies, including refreshing their external rating
 */
public interface MovieService {

    /**
     * @return all movies in the library
     */
    List<MovieResponseDTO> getAllMovies();

    /**
     * @param id id of the movie to retrieve
     * @return the matching movie
     * @throws com.movielibrary.exception.MovieNotFoundException if no movie has that id
     */
    MovieResponseDTO getMovieById(Long id);

    /**
     * Creates a movie and kicks off an asynchronous lookup of its external rating
     *
     * @param request the movie to create
     * @return the created movie
     */
    MovieResponseDTO createMovie(MovieRequestDTO request);

    /**
     * @param id      id of the movie to update
     * @param request the new movie data
     * @return the updated movie
     * @throws com.movielibrary.exception.MovieNotFoundException if no movie has that id
     */
    MovieResponseDTO updateMovie(Long id, MovieRequestDTO request);

    /**
     * @param id id of the movie to delete
     * @throws com.movielibrary.exception.MovieNotFoundException if no movie has that id
     */
    void deleteMovie(Long id);

    /**
     * Synchronously re-fetches the movie's external rating and persists it
     *
     * @param id id of the movie to refresh
     * @return the movie with its refreshed rating
     * @throws com.movielibrary.exception.MovieNotFoundException if no movie has that id
     */
    MovieResponseDTO refreshRating(Long id);
}
