package com.movielibrary.service;

import com.movielibrary.dto.UserRequestDTO;
import com.movielibrary.dto.UserResponseDTO;

import java.util.List;

/**
 * Business operations for managing users and their assigned roles
 */
public interface UserService {

    /**
     * @return all users
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * @param id id of the user to retrieve
     * @return the matching user
     * @throws com.movielibrary.exception.UserNotFoundException if no user has that id
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Creates a user, hashing the supplied password and resolving the requested role ids.
     *
     * @param request the user to create
     * @return the created user
     * @throws com.movielibrary.exception.RoleNotFoundException if a requested role id does not exist
     */
    UserResponseDTO createUser(UserRequestDTO request);

    /**
     * @param id      id of the user to update
     * @param request the new user data
     * @return the updated user
     * @throws com.movielibrary.exception.UserNotFoundException if no user has that id
     * @throws com.movielibrary.exception.RoleNotFoundException if a requested role id does not exist
     */
    UserResponseDTO updateUser(Long id, UserRequestDTO request);

    /**
     * @param id id of the user to delete
     * @throws com.movielibrary.exception.UserNotFoundException if no user has that id
     */
    void deleteUser(Long id);
}
