package com.movielibrary.service;

import com.movielibrary.dto.MovieRequestDTO;
import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.model.Movie;
import com.movielibrary.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final RatingEnrichmentService ratingEnrichmentService;

    public MovieServiceImpl(MovieRepository movieRepository, RatingEnrichmentService ratingEnrichmentService) {
        this.movieRepository = movieRepository;
        this.ratingEnrichmentService = ratingEnrichmentService;
    }

    @Override
    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieResponseDTO::fromEntity)
                .toList();
    }

    @Override
    public MovieResponseDTO getMovieById(Long id) {
        return MovieResponseDTO.fromEntity(findMovieEntity(id));
    }

    @Override
    public MovieResponseDTO createMovie(MovieRequestDTO request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDirector(request.getDirector());
        movie.setReleaseYear(request.getReleaseYear());
        Movie saved = movieRepository.save(movie);
        ratingEnrichmentService.enrichRatingAsync(saved.getId(), saved.getTitle());
        return MovieResponseDTO.fromEntity(saved);
    }

    @Override
    public MovieResponseDTO updateMovie(Long id, MovieRequestDTO request) {
        Movie existing = findMovieEntity(id);
        existing.setTitle(request.getTitle());
        existing.setDirector(request.getDirector());
        existing.setReleaseYear(request.getReleaseYear());
        return MovieResponseDTO.fromEntity(movieRepository.save(existing));
    }

    @Override
    public void deleteMovie(Long id) {
        Movie existing = findMovieEntity(id);
        movieRepository.delete(existing);
    }

    @Override
    public MovieResponseDTO refreshRating(Long id) {
        return ratingEnrichmentService.refreshRating(id);
    }

    private Movie findMovieEntity(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + id));
    }
}
