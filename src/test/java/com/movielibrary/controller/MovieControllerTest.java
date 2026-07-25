package com.movielibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movielibrary.dto.MovieRequestDTO;
import com.movielibrary.dto.MovieResponseDTO;
import com.movielibrary.exception.MovieNotFoundException;
import com.movielibrary.security.CustomUserDetailsService;
import com.movielibrary.security.JwtAccessDeniedHandler;
import com.movielibrary.security.JwtAuthenticationEntryPoint;
import com.movielibrary.security.JwtAuthenticationFilter;
import com.movielibrary.security.JwtService;
import com.movielibrary.security.SecurityConfig;
import com.movielibrary.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private MovieResponseDTO sampleMovie() {
        return new MovieResponseDTO(1L, "The Matrix", "Wachowski", 1999, 8.7, null, null);
    }

    @Test
    void getAllMovies_anonymous_returns401() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllMovies_asUser_returns200() throws Exception {
        when(movieService.getAllMovies()).thenReturn(List.of(sampleMovie()));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("The Matrix"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMovies_asAdmin_returns200() throws Exception {
        when(movieService.getAllMovies()).thenReturn(List.of(sampleMovie()));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMovieById_notFound_returns404() throws Exception {
        when(movieService.getMovieById(99L)).thenThrow(new MovieNotFoundException(99L));

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie not found: 99"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovie_asUser_returns403() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Inception");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMovie_asAdmin_returns201() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Inception");
        request.setDirector("Nolan");
        request.setReleaseYear(2010);

        when(movieService.createMovie(any(MovieRequestDTO.class)))
                .thenReturn(new MovieResponseDTO(2L, "Inception", "Nolan", 2010, null, null, null));

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMovie_blankTitle_returns400() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle(" ");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMovie_asAdmin_returns200() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("The Matrix Reloaded");
        request.setReleaseYear(2003);

        when(movieService.updateMovie(eq(1L), any(MovieRequestDTO.class)))
                .thenReturn(new MovieResponseDTO(1L, "The Matrix Reloaded", "Wachowski", 2003, null, null, null));

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Matrix Reloaded"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteMovie_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isForbidden());

        verify(movieService, org.mockito.Mockito.never()).deleteMovie(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMovie_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void refreshRating_asUser_returns403() throws Exception {
        mockMvc.perform(post("/api/movies/1/refresh-rating"))
                .andExpect(status().isForbidden());
    }
}
