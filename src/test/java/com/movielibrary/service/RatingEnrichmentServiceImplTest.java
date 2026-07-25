package com.movielibrary.service;

import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.exception.MovieNotFoundException;
import com.movielibrary.external.OmdbClient;
import com.movielibrary.model.Movie;
import com.movielibrary.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingEnrichmentServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private OmdbClient omdbClient;

    private RatingEnrichmentServiceImpl ratingEnrichmentService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        ratingEnrichmentService = new RatingEnrichmentServiceImpl(movieRepository, omdbClient);
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("The Matrix");
    }

    @Test
    void enrichRatingAsync_ratingFound_updatesAndSavesMovie() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(omdbClient.fetchRating("The Matrix")).thenReturn(Optional.of(8.7));

        ratingEnrichmentService.enrichRatingAsync(1L, "The Matrix");

        assertThat(movie.getRating()).isEqualTo(8.7);
        verify(movieRepository).save(movie);
    }

    @Test
    void enrichRatingAsync_noRatingFound_doesNotSave() {
        when(omdbClient.fetchRating("The Matrix")).thenReturn(Optional.empty());

        ratingEnrichmentService.enrichRatingAsync(1L, "The Matrix");

        assertThat(movie.getRating()).isNull();
        verify(movieRepository, never()).findById(any());
        verify(movieRepository, never()).save(any());
    }

    @Test
    void enrichRatingAsync_movieDeletedBeforeEnrichmentCompletes_doesNothing() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        when(omdbClient.fetchRating("The Matrix")).thenReturn(Optional.of(8.7));

        ratingEnrichmentService.enrichRatingAsync(1L, "The Matrix");

        verify(movieRepository, never()).save(any());
    }

    @Test
    void refreshRating_movieFound_updatesRatingAndReturnsDto() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(omdbClient.fetchRating("The Matrix")).thenReturn(Optional.of(9.0));
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieResponseDTO result = ratingEnrichmentService.refreshRating(1L);

        assertThat(result.getRating()).isEqualTo(9.0);
        verify(movieRepository).save(movie);
    }

    @Test
    void refreshRating_movieNotFound_throws() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingEnrichmentService.refreshRating(99L))
                .isInstanceOf(MovieNotFoundException.class);
    }
}
