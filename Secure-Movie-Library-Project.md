# Secure Movie Library & Background Rating Aggregation

## Project Description

Build a secure Spring Boot application that manages a catalog of movies and enriches them with aggregated rating data retrieved asynchronously from an external service (e.g., OMDb API, TMDB API).

## Functional Requirements

### 1. Movie Library Management API

Create a REST API with CRUD operations for managing movies.

Each movie should include:

- ID (auto-generated)
- Title (required)
- Director
- Release year
- Rating (may be null initially, will be enriched later)

#### Required Endpoints

- Create a new movie
- Fetch all movies
- Fetch a specific movie
- Update movie data
- Delete a movie

Apply proper validation (e.g., title non-empty, reasonable release year).

### 2. External Data Enrichment

When a new movie is created:

- Use the movie title to query a public external movie data API (OMDb, TMDB, etc.).
- If data is found, extract an appropriate rating score.
- Save the rating into the local movie record.

#### Requirements

- Rating lookup must run in the background.
- The POST request must return immediately with the movie data without waiting for the enrichment.

### 3. Asynchronous Processing

The external API request must not block the creation endpoint.

## Non-Functional Requirements

### 1. Security

- The API must be secured.
- Use authorization mechanism by your choice.
- Define at least two roles: ADMIN and USER.

#### Authorization Rules

- **ADMIN:** Allowed to perform all CRUD operations
- **USER:** Allowed read-only access

### 2. Implementation & Design

- Use Java and Spring Boot.
- Use a persistence mechanism by your choice.
- Valid input handling
- Demonstrate clean, consistent error responses

## Deliverables

- GitHub repository with:
  - Spring Boot project
  - Clear structure and readable code
  - Integration or unit tests
- A short technical document (1-2 pages) describing:
  - The external rating API chosen
  - How authentication & authorization are implemented
  - How asynchronous enrichment works
  - Any architectural decisions or trade-offs
- Swagger documentation
