package com.movielibrary.repository;

import com.movielibrary.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link User} entities
 */
public interface UserRepository {

    /**
     * @return all users
     */
    List<User> findAll();

    /**
     * @param id id of the user to find
     * @return the matching user, or {@link Optional#empty()} if none exists
     */
    Optional<User> findById(Long id);

    /**
     * @param username username to search for
     * @return the matching user, or {@link Optional#empty()} if none exists
     */
    Optional<User> findByUsername(String username);

    /**
     * Inserts a new user or updates an existing one, depending on whether it has an id
     *
     * @param user the user to persist
     * @return the persisted user
     */
    User save(User user);

    /**
     * @param user the user to remove
     */
    void delete(User user);
}
