package com.movielibrary.dto;

import com.movielibrary.model.Movie;

import java.time.LocalDateTime;

public class MovieResponseDTO {

    private final Long id;
    private final String title;
    private final String director;
    private final Integer releaseYear;
    private final Double rating;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public MovieResponseDTO(Long id, String title, String director, Integer releaseYear, Double rating,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MovieResponseDTO fromEntity(Movie movie) {
        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear(),
                movie.getRating(),
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Double getRating() {
        return rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
