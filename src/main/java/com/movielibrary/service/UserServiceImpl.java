package com.movielibrary.service;

import com.movielibrary.model.Role;
import com.movielibrary.model.User;
import com.movielibrary.repository.RoleRepository;
import com.movielibrary.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    @Override
    public User createUser(User user) {
        user.setId(null);
        user.setRoles(resolveRoles(user.getRoles()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User update) {
        User existing = getUserById(id);
        existing.setUsername(update.getUsername());
        existing.setPassword(update.getPassword());
        existing.setEnabled(update.isEnabled());
        existing.setRoles(resolveRoles(update.getRoles()));
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }

    private Set<Role> resolveRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(role -> roleRepository.findById(role.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + role.getId())))
                .collect(Collectors.toSet());
    }
}
