package com.movielibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieLibraryApplication.class, args);
    }
}
