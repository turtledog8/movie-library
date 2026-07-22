package com.movielibrary.repository;

import com.movielibrary.model.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class MovieRepositoryImpl implements MovieRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Movie> findAll() {
        return entityManager.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Movie.class, id));
    }

    @Override
    @Transactional
    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            entityManager.persist(movie);
            return movie;
        }
        return entityManager.merge(movie);
    }

    @Override
    @Transactional
    public void delete(Movie movie) {
        entityManager.remove(entityManager.contains(movie) ? movie : entityManager.merge(movie));
    }
}
