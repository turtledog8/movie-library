package com.movielibrary.repository;

import com.movielibrary.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MovieRepositoryImpl.class)
class MovieRepositoryImplTest {

    @Autowired
    private MovieRepositoryImpl movieRepository;

    @Test
    void save_newMovie_assignsGeneratedId() {
        Movie movie = new Movie();
        movie.setTitle("The Matrix");
        movie.setDirector("Wachowski");
        movie.setReleaseYear(1999);

        Movie saved = movieRepository.save(movie);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findById_existingMovie_returnsMovie() {
        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setReleaseYear(2010);
        Movie saved = movieRepository.save(movie);

        Optional<Movie> found = movieRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Inception");
    }

    @Test
    void findById_missingMovie_returnsEmpty() {
        assertThat(movieRepository.findById(12345L)).isEmpty();
    }

    @Test
    void findAll_returnsAllSavedMovies() {
        Movie first = new Movie();
        first.setTitle("Movie One");
        Movie second = new Movie();
        second.setTitle("Movie Two");
        movieRepository.save(first);
        movieRepository.save(second);

        List<Movie> all = movieRepository.findAll();

        assertThat(all).extracting(Movie::getTitle).containsExactlyInAnyOrder("Movie One", "Movie Two");
    }

    @Test
    void save_existingMovie_updatesFields() {
        Movie movie = new Movie();
        movie.setTitle("Original Title");
        Movie saved = movieRepository.save(movie);

        saved.setTitle("Updated Title");
        movieRepository.save(saved);

        Optional<Movie> found = movieRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void delete_removesMovie() {
        Movie movie = new Movie();
        movie.setTitle("To Delete");
        Movie saved = movieRepository.save(movie);

        movieRepository.delete(saved);

        assertThat(movieRepository.findById(saved.getId())).isEmpty();
    }
}
