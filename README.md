# Secure Movie Library

A Spring Boot REST API for managing a catalog of movies, secured with 
JWT-based authentication/authorization and enriched in the background 
with IMDb ratings pulled from OMDb.

This is my Final Progress Review project to complete Telerik Alpha's
Java program, built within a week.

## Running it

Requires a MariaDB instance with a `movie_library` database already 
created (`CREATE DATABASE movie_library;`) - Flyway migrates the schema 
from there. DB/JWT/OMDb config is read from environment variables, see `application.yml`.

```
mvn spring-boot:run
```

## Docs

- [Secure-Movie-Library-Project.md](Secure-Movie-Library-Project.md) - 
  the original project requirements


- [TECHNICAL_OVERVIEW.md](TECHNICAL_OVERVIEW.md) - 
Serves as documentation and a needed deliverable.
