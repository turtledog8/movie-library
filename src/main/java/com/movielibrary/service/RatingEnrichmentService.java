package com.movielibrary.service;

import com.movielibrary.dto.MovieResponseDTO;

/**
 * Looks up and persists a movie's external (IMDb) rating
 */
public interface RatingEnrichmentService {

    /**
     * Fetches the rating for a movie in the background and saves it if found
     * Errors and not-found results are silently ignored.
     *
     * @param movieId id of the movie to enrich
     * @param title   title to search for in the external rating source
     */
    void enrichRatingAsync(Long movieId, String title);

    /**
     * Synchronously fetches and persists the rating for a movie
     *
     * @param movieId id of the movie to refresh
     * @return the movie with its refreshed rating
     * @throws com.movielibrary.exception.MovieNotFoundException if no movie has that id
     */
    MovieResponseDTO refreshRating(Long movieId);
}
