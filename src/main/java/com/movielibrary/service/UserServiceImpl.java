package com.movielibrary.service;

import com.movielibrary.dto.UserRequestDTO;
import com.movielibrary.dto.UserResponseDTO;
import com.movielibrary.model.Role;
import com.movielibrary.model.User;
import com.movielibrary.repository.RoleRepository;
import com.movielibrary.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return UserResponseDTO.fromEntity(findUserEntity(id));
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.isEnabled());
        user.setRoles(resolveRoles(request.getRoleIds()));
        return UserResponseDTO.fromEntity(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User existing = findUserEntity(id);
        existing.setUsername(request.getUsername());
        existing.setPassword(passwordEncoder.encode(request.getPassword()));
        existing.setEnabled(request.isEnabled());
        existing.setRoles(resolveRoles(request.getRoleIds()));
        return UserResponseDTO.fromEntity(userRepository.save(existing));
    }

    @Override
    public void deleteUser(Long id) {
        User existing = findUserEntity(id);
        userRepository.delete(existing);
    }

    private User findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }
        return roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleId)))
                .collect(Collectors.toSet());
    }
}
