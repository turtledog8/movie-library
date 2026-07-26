package com.movielibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Movie Library Spring Boot application
 */
@SpringBootApplication
@EnableAsync
public class MovieLibraryApplication {

    /**
     * Boots the Spring application context
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(MovieLibraryApplication.class, args);
    }
}
