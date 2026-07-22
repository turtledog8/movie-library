package com.movielibrary.config;

import com.movielibrary.model.Role;
import com.movielibrary.model.User;
import com.movielibrary.repository.RoleRepository;
import com.movielibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(RoleRepository roleRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.username}") String adminUsername,
                            @Value("${app.admin.password}") String adminPassword) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        Role adminRole = findOrCreateRole("ADMIN");
        findOrCreateRole("USER");

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(java.util.List.of(adminRole)));
            userRepository.save(admin);
        }
    }

    private Role findOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }
}
