package com.movielibrary.repository;

import com.movielibrary.model.Role;
import com.movielibrary.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UserRepositoryImpl.class, RoleRepositoryImpl.class})
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private RoleRepositoryImpl roleRepository;

    @Test
    void save_newUser_assignsGeneratedId() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("hashed");
        user.setEnabled(true);

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByUsername_existingUser_returnsUser() {
        User user = new User();
        user.setUsername("jane");
        user.setPassword("hashed");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("jane");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("jane");
    }

    @Test
    void findByUsername_missingUser_returnsEmpty() {
        assertThat(userRepository.findByUsername("nobody")).isEmpty();
    }

    @Test
    void save_userWithRoles_persistsRoleAssociation() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Role saved = roleRepository.save(adminRole);

        User user = new User();
        user.setUsername("admin-user");
        user.setPassword("hashed");
        user.setRoles(Set.of(saved));
        User savedUser = userRepository.save(user);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getRoles()).extracting(Role::getName).containsExactly("ADMIN");
    }

    @Test
    void delete_removesUser() {
        User user = new User();
        user.setUsername("to-delete");
        user.setPassword("hashed");
        User saved = userRepository.save(user);

        userRepository.delete(saved);

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}
