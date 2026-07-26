package com.movielibrary.service;

import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.exception.MovieNotFoundException;
import com.movielibrary.external.OmdbClient;
import com.movielibrary.model.Movie;
import com.movielibrary.repository.MovieRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * {@link RatingEnrichmentService} implementation backed by {@link OmdbClient}
 */
@Service
public class RatingEnrichmentServiceImpl implements RatingEnrichmentService {

    private final MovieRepository movieRepository;
    private final OmdbClient omdbClient;

    public RatingEnrichmentServiceImpl(MovieRepository movieRepository, OmdbClient omdbClient) {
        this.movieRepository = movieRepository;
        this.omdbClient = omdbClient;
    }

    /**
     * Runs on Spring's async executor; the movie is only updated if both the movie
     * still exists and OMDb returned a rating
     */
    @Override
    @Async
    public void enrichRatingAsync(Long movieId, String title) {
        omdbClient.fetchRating(title).ifPresent(rating ->
                movieRepository.findById(movieId).ifPresent(movie -> {
                    movie.setRating(rating);
                    movieRepository.save(movie);
                })
        );
    }

    @Override
    public MovieResponseDTO refreshRating(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        omdbClient.fetchRating(movie.getTitle()).ifPresent(movie::setRating);

        return MovieResponseDTO.fromEntity(movieRepository.save(movie));
    }
}
