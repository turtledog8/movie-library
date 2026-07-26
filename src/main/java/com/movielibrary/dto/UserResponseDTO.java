package com.movielibrary.dto;

import com.movielibrary.model.Role;
import com.movielibrary.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * User representation returned by the API with role names in place of role entities
 */
public class UserResponseDTO {

    private final Long id;
    private final String username;
    private final boolean enabled;
    private final Set<String> roles;

    public UserResponseDTO(Long id, String username, boolean enabled, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.roles = roles;
    }

    /**
     * Builds a response DTO from a {@link User} entity, flattening roles to their names
     *
     * @param user the entity to convert
     * @return the corresponding response DTO
     */
    public static UserResponseDTO fromEntity(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserResponseDTO(user.getId(), user.getUsername(), user.isEnabled(), roleNames);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
