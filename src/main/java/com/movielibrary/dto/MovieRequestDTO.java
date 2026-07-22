package com.movielibrary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class MovieRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String director;

    @Min(value = 1888, message = "Release year must be 1888 or later")
    @Max(value = 2100, message = "Release year must be 2100 or earlier")
    private Integer releaseYear;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
}
