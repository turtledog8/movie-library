package com.movielibrary.repository;

import com.movielibrary.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link Role} entities.
 */
public interface RoleRepository {

    /**
     * @return all roles
     */
    List<Role> findAll();

    /**
     * @param id id of the role to find
     * @return the matching role, or {@link Optional#empty()} if none exists
     */
    Optional<Role> findById(Long id);

    /**
     * @param name role name to search for
     * @return the matching role, or {@link Optional#empty()} if none exists
     */
    Optional<Role> findByName(String name);

    /**
     * Inserts a new role or updates an existing one, depending on whether it has an id
     *
     * @param role the role to persist
     * @return the persisted role
     */
    Role save(Role role);
}
