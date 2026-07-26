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

/**
 * Seeds the database with the base roles and a default admin user on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    /**
     * Creates the initializer.
     *
     * @param roleRepository  repository used to look up and persist roles
     * @param userRepository  repository used to look up and persist users
     * @param passwordEncoder encoder used to hash the default admin password
     * @param adminUsername   username for the default admin account, from {@code app.admin.username}
     * @param adminPassword   raw password for the default admin account, from {@code app.admin.password}
     */
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

    /**
     * Ensures the {@code ADMIN} and {@code USER} roles exist and creates the default
     * admin user if it is not already present.
     *
     * @param args command-line arguments (unused)
     */
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

    /**
     * Looks up a role by name, creating and persisting it if it does not exist.
     *
     * @param name role name to find or create
     * @return the existing or newly created role
     */
    private Role findOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }
}
