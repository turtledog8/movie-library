package com.movielibrary.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class OmdbClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public OmdbClient(RestTemplate restTemplate,
                       @Value("${omdb.base-url}") String baseUrl,
                       @Value("${omdb.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public Optional<Double> fetchRating(String title) {
        try {
            OmdbSearchResponse response = restTemplate.getForObject(
                    baseUrl + "/?t={title}&apikey={apiKey}",
                    OmdbSearchResponse.class,
                    title, apiKey);

            if (response == null || !"True".equalsIgnoreCase(response.response)
                    || response.imdbRating == null || "N/A".equalsIgnoreCase(response.imdbRating)) {
                return Optional.empty();
            }
            return Optional.of(Double.parseDouble(response.imdbRating));
        } catch (RestClientException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OmdbSearchResponse {

        @JsonProperty("imdbRating")
        private String imdbRating;

        @JsonProperty("Response")
        private String response;
    }
}
