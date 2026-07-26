package com.movielibrary.controller;

import com.movielibrary.dto.MovieRequestDTO;
import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.service.MovieService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for managing movies. All endpoints require a valid bearer JWT
 */
@RestController
@RequestMapping("/api/movies")
@SecurityRequirement(name = "bearerAuth")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Retrieves all movies in the library
     *
     * @return the list of movies
     */
    @GetMapping
    public List<MovieResponseDTO> getAllMovies() {
        return movieService.getAllMovies();
    }

    /**
     * Retrieves a single movie by its id
     *
     * @param id id of the movie to retrieve
     * @return the matching movie
     */
    @GetMapping("/{id}")
    public MovieResponseDTO getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    /**
     * Creates a new movie
     *
     * @param request the movie to create
     * @return the created movie, with a {@code 201 Created} status
     */
    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO request) {
        MovieResponseDTO created = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing movie
     *
     * @param id      id of the movie to update
     * @param request the new movie data
     * @return the updated movie
     */
    @PutMapping("/{id}")
    public MovieResponseDTO updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequestDTO request) {
        return movieService.updateMovie(id, request);
    }

    /**
     * Deletes a movie by id
     *
     * @param id id of the movie to delete
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Re-fetches and updates the external rating for a movie
     *
     * @param id id of the movie to refresh
     * @return the movie with its refreshed rating
     */
    @PostMapping("/{id}/refresh-rating")
    public MovieResponseDTO refreshRating(@PathVariable Long id) {
        return movieService.refreshRating(id);
    }
}
