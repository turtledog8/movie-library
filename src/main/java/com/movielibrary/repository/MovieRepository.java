package com.movielibrary.repository;

import com.movielibrary.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie save(Movie movie);

    void delete(Movie movie);
}
