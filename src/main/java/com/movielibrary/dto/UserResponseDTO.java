package com.movielibrary.dto;

import com.movielibrary.model.Role;
import com.movielibrary.model.User;

import java.util.Set;
import java.util.stream.Collectors;

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
