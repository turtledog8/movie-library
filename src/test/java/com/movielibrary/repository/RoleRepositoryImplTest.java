package com.movielibrary.repository;

import com.movielibrary.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(RoleRepositoryImpl.class)
class RoleRepositoryImplTest {

    @Autowired
    private RoleRepositoryImpl roleRepository;

    @Test
    void save_newRole_assignsGeneratedId() {
        Role role = new Role();
        role.setName("USER");

        Role saved = roleRepository.save(role);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByName_existingRole_returnsRole() {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName("ADMIN");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    void findByName_missingRole_returnsEmpty() {
        assertThat(roleRepository.findByName("NOT_A_ROLE")).isEmpty();
    }
}
