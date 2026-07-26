package com.movielibrary.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OmdbClientTest {

    private static final String BASE_URL = "http://localhost/omdb";
    private static final String API_KEY = "test-key";

    private MockRestServiceServer server;
    private OmdbClient omdbClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        omdbClient = new OmdbClient(restTemplate, BASE_URL, API_KEY);
    }

    @Test
    void fetchRating_ratingFound_returnsRating() {
        server.expect(requestTo(BASE_URL + "/?t=Matrix&apikey=" + API_KEY))
                .andRespond(withSuccess("{\"imdbRating\":\"8.7\",\"Response\":\"True\"}", MediaType.APPLICATION_JSON));

        Optional<Double> result = omdbClient.fetchRating("Matrix");

        assertThat(result).contains(8.7);
    }

    @Test
    void fetchRating_movieNotFound_returnsEmpty() {
        server.expect(requestTo(BASE_URL + "/?t=Nonexistent&apikey=" + API_KEY))
                .andRespond(withSuccess("{\"Response\":\"False\"}", MediaType.APPLICATION_JSON));

        Optional<Double> result = omdbClient.fetchRating("Nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchRating_noRatingInResponse_returnsEmpty() {
        server.expect(requestTo(BASE_URL + "/?t=NoRating&apikey=" + API_KEY))
                .andRespond(withSuccess("{\"imdbRating\":\"N/A\",\"Response\":\"True\"}", MediaType.APPLICATION_JSON));

        Optional<Double> result = omdbClient.fetchRating("NoRating");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchRating_malformedRating_returnsEmpty() {
        server.expect(requestTo(BASE_URL + "/?t=Weird&apikey=" + API_KEY))
                .andRespond(withSuccess("{\"imdbRating\":\"not-a-number\",\"Response\":\"True\"}", MediaType.APPLICATION_JSON));

        Optional<Double> result = omdbClient.fetchRating("Weird");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchRating_serverError_returnsEmpty() {
        server.expect(requestTo(BASE_URL + "/?t=Broken&apikey=" + API_KEY))
                .andRespond(withServerError());

        Optional<Double> result = omdbClient.fetchRating("Broken");

        assertThat(result).isEmpty();
    }
}
