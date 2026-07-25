package com.movielibrary.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-key-not-used-anywhere-else-32bytes";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L);
        userDetails = User.withUsername("john")
                .password("irrelevant")
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .build();
    }

    @Test
    void generateToken_thenExtractUsername_returnsSameUsername() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("john");
    }

    @Test
    void isTokenValid_matchingUser_returnsTrue() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_differentUser_returnsFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.withUsername("someone-else").password("x").authorities(Collections.<SimpleGrantedAuthority>emptyList()).build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        JwtService shortLivedJwtService = new JwtService(SECRET, -1_000L);
        String expiredToken = shortLivedJwtService.generateToken(userDetails);

        assertThat(shortLivedJwtService.isTokenValid(expiredToken, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_malformedToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("not-a-real-token", userDetails)).isFalse();
    }
}
