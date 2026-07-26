package com.movielibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movielibrary.dto.UserRequestDTO;
import com.movielibrary.dto.UserResponseDTO;
import com.movielibrary.security.CustomUserDetailsService;
import com.movielibrary.security.JwtAccessDeniedHandler;
import com.movielibrary.security.JwtAuthenticationEntryPoint;
import com.movielibrary.security.JwtAuthenticationFilter;
import com.movielibrary.security.JwtService;
import com.movielibrary.security.SecurityConfig;
import com.movielibrary.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getAllUsers_anonymous_returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_asUser_returns403() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_asAdmin_returns200() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserResponseDTO(1L, "john", true, Set.of("ADMIN"))));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_asAdmin_returns201() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("jane");
        request.setPassword("secret123");

        when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(new UserResponseDTO(2L, "jane", true, Set.of()));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("jane"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_asUser_returns403() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("jane");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_blankUsername_returns400() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername(" ");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_asAdmin_returns200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(new UserResponseDTO(1L, "john", true, Set.of("ADMIN")));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserById_asUser_returns403() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_asAdmin_returns200() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("john-updated");
        request.setPassword("secret123");

        when(userService.updateUser(eq(1L), any(UserRequestDTO.class)))
                .thenReturn(new UserResponseDTO(1L, "john-updated", true, Set.of("ADMIN")));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john-updated"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_asUser_returns403() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("john-updated");
        request.setPassword("secret123");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(any());
    }
}
