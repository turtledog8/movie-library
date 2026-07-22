package com.movielibrary.service;

import com.movielibrary.dto.MovieRequestDTO;
import com.movielibrary.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {

    List<MovieResponseDTO> getAllMovies();

    MovieResponseDTO getMovieById(Long id);

    MovieResponseDTO createMovie(MovieRequestDTO request);

    MovieResponseDTO updateMovie(Long id, MovieRequestDTO request);

    void deleteMovie(Long id);

    MovieResponseDTO refreshRating(Long id);
}
