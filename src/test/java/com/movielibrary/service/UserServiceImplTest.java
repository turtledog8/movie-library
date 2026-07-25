package com.movielibrary.service;

import com.movielibrary.dto.UserRequestDTO;
import com.movielibrary.dto.UserResponseDTO;
import com.movielibrary.exception.RoleNotFoundException;
import com.movielibrary.exception.UserNotFoundException;
import com.movielibrary.model.Role;
import com.movielibrary.model.User;
import com.movielibrary.repository.RoleRepository;
import com.movielibrary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    private User user;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);

        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("encoded-pass");
        user.setEnabled(true);
        user.setRoles(Set.of(adminRole));
    }

    @Test
    void getAllUsers_returnsMappedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john");
    }

    @Test
    void getUserById_found_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result.getUsername()).isEqualTo("john");
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createUser_encodesPasswordAndResolvesRoles() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("jane");
        request.setPassword("plain-pass");
        request.setEnabled(true);
        request.setRoleIds(Set.of(1L));

        when(passwordEncoder.encode("plain-pass")).thenReturn("hashed-pass");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        UserResponseDTO result = userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed-pass");
        assertThat(captor.getValue().getRoles()).containsExactly(adminRole);
        assertThat(result.getUsername()).isEqualTo("jane");
        assertThat(result.getRoles()).containsExactly("ADMIN");
    }

    @Test
    void createUser_withNoRoleIds_savesWithEmptyRoles() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("jane");
        request.setPassword("plain-pass");
        request.setRoleIds(Set.of());

        when(passwordEncoder.encode("plain-pass")).thenReturn("hashed-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRoles()).isEmpty();
        verify(roleRepository, never()).findById(any());
    }

    @Test
    void createUser_unknownRoleId_throws() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("jane");
        request.setPassword("plain-pass");
        request.setRoleIds(Set.of(99L));

        when(passwordEncoder.encode("plain-pass")).thenReturn("hashed-pass");
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(RoleNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_found_reEncodesPasswordAndUpdatesFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-pass")).thenReturn("new-hashed");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("john-updated");
        request.setPassword("new-pass");
        request.setEnabled(false);
        request.setRoleIds(Set.of(1L));

        UserResponseDTO result = userService.updateUser(1L, request);

        assertThat(result.getUsername()).isEqualTo("john-updated");
        assertThat(result.isEnabled()).isFalse();
        assertThat(user.getPassword()).isEqualTo("new-hashed");
    }

    @Test
    void updateUser_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("whoever");
        request.setPassword("pass");

        assertThatThrownBy(() -> userService.updateUser(99L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_found_deletes() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any());
    }
}
