package com.movielibrary.service;

import com.movielibrary.dto.MovieResponseDTO;

public interface RatingEnrichmentService {

    void enrichRatingAsync(Long movieId, String title);

    MovieResponseDTO refreshRating(Long movieId);
}
