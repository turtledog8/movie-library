package com.movielibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Provides the {@link RestTemplate} bean used to call external OMDb API
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a default {@link RestTemplate} instance
     *
     * @return a new {@code RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
