package com.movielibrary.service;

import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.external.OmdbClient;
import com.movielibrary.model.Movie;
import com.movielibrary.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RatingEnrichmentServiceImpl implements RatingEnrichmentService {

    private final MovieRepository movieRepository;
    private final OmdbClient omdbClient;

    public RatingEnrichmentServiceImpl(MovieRepository movieRepository, OmdbClient omdbClient) {
        this.movieRepository = movieRepository;
        this.omdbClient = omdbClient;
    }

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + movieId));

        omdbClient.fetchRating(movie.getTitle()).ifPresent(movie::setRating);

        return MovieResponseDTO.fromEntity(movieRepository.save(movie));
    }
}
