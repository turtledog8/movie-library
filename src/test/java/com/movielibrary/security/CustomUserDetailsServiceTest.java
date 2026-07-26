package com.movielibrary.security;

import com.movielibrary.model.Role;
import com.movielibrary.model.User;
import com.movielibrary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_existingUser_mapsRolesToRolePrefixedAuthorities() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Role userRole = new Role();
        userRole.setName("USER");

        User user = new User();
        user.setUsername("john");
        user.setPassword("hashed-pass");
        user.setEnabled(true);
        user.setRoles(Set.of(adminRole, userRole));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("john");

        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("hashed-pass");
        assertThat(result.getAuthorities())
                .extracting(Object::toString)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void loadUserByUsername_disabledUser_returnsDisabledUserDetails() {
        User user = new User();
        user.setUsername("disabled-user");
        user.setPassword("hashed-pass");
        user.setEnabled(false);
        user.setRoles(Set.of());

        when(userRepository.findByUsername("disabled-user")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("disabled-user");

        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_missingUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nobody"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
