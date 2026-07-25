package com.movielibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movielibrary.dto.LoginRequestDTO;
import com.movielibrary.security.CustomUserDetailsService;
import com.movielibrary.security.JwtAccessDeniedHandler;
import com.movielibrary.security.JwtAuthenticationEntryPoint;
import com.movielibrary.security.JwtAuthenticationFilter;
import com.movielibrary.security.JwtService;
import com.movielibrary.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");

        User principal = new User("admin", "encoded", java.util.List.of());
        UsernamePasswordAuthenticationToken authenticated =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authenticated);
        when(jwtService.generateToken(principal)).thenReturn("fake.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_missingFields_returns400() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
