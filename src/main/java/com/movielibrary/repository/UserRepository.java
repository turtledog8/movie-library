package com.movielibrary.repository;

import com.movielibrary.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);
}
