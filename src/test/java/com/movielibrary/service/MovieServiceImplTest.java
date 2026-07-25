package com.movielibrary.service;

import com.movielibrary.dto.MovieRequestDTO;
import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.exception.MovieNotFoundException;
import com.movielibrary.model.Movie;
import com.movielibrary.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RatingEnrichmentService ratingEnrichmentService;

    private MovieServiceImpl movieService;

    private Movie movie;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        movieService = new MovieServiceImpl(movieRepository, ratingEnrichmentService);
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("The Matrix");
        movie.setDirector("Wachowski");
        movie.setReleaseYear(1999);
    }

    @Test
    void getAllMovies_returnsMappedMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<MovieResponseDTO> result = movieService.getAllMovies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("The Matrix");
    }

    @Test
    void getMovieById_found_returnsMovie() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        MovieResponseDTO result = movieService.getMovieById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("The Matrix");
    }

    @Test
    void getMovieById_notFound_throws() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMovieById(99L))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    void createMovie_savesAndTriggersAsyncEnrichment() {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Inception");
        request.setDirector("Nolan");
        request.setReleaseYear(2010);

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        MovieResponseDTO result = movieService.createMovie(request);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getTitle()).isEqualTo("Inception");

        verify(ratingEnrichmentService).enrichRatingAsync(42L, "Inception");
    }

    @Test
    void updateMovie_found_updatesFields() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("The Matrix Reloaded");
        request.setDirector("Wachowski");
        request.setReleaseYear(2003);

        MovieResponseDTO result = movieService.updateMovie(1L, request);

        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("The Matrix Reloaded");
        assertThat(captor.getValue().getReleaseYear()).isEqualTo(2003);
        assertThat(result.getTitle()).isEqualTo("The Matrix Reloaded");
    }

    @Test
    void updateMovie_notFound_throws() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Whatever");

        assertThatThrownBy(() -> movieService.updateMovie(99L, request))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository, never()).save(any());
    }

    @Test
    void deleteMovie_found_deletes() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        verify(movieRepository).delete(movie);
    }

    @Test
    void deleteMovie_notFound_throws() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.deleteMovie(99L))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository, never()).delete(any());
    }

    @Test
    void refreshRating_delegatesToRatingEnrichmentService() {
        MovieResponseDTO expected = new MovieResponseDTO(1L, "The Matrix", "Wachowski", 1999, 8.7, null, null);
        when(ratingEnrichmentService.refreshRating(1L)).thenReturn(expected);

        MovieResponseDTO result = movieService.refreshRating(1L);

        assertThat(result).isEqualTo(expected);
    }
}
