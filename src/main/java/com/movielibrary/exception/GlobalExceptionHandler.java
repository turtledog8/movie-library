package com.movielibrary.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Translates exceptions thrown anywhere in the request-handling pipeline into a
 * consistent {@link ErrorResponse} JSON body with the appropriate HTTP status
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles a missing movie lookup
     *
     * @param e       the thrown exception
     * @param request the originating request
     * @return a {@code 404 Not Found} error response
     */
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMovieNotFound(MovieNotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    /**
     * Handles a missing user lookup
     *
     * @param e       the thrown exception
     * @param request the originating request
     * @return a {@code 404 Not Found} error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    /**
     * Handles a missing role lookup, e.g. when a request references a non-existent role id
     *
     * @param e       the thrown exception
     * @param request the originating request
     * @return a {@code 400 Bad Request} error response
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(RoleNotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    /**
     * Handles a failed login attempt
     *
     * @param e       the thrown exception
     * @param request the originating request
     * @return a {@code 401 Unauthorized} error response
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    /**
     * Handles bean validation failures on request bodies, concatenating all field errors
     * into a single message
     *
     * @param e       the thrown exception
     * @param request the originating request
     * @return a {@code 400 Bad Request} error response listing the failed fields
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * Fallback handler for any exception not covered by a more specific handler
     *
     * @param e       the thrown exception
     * @param request the originating request
     * @return a {@code 500 Internal Server Error} response with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception on {}", request.getRequestURI(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

    /**
     * Assembles the {@link ErrorResponse} body shared by all handlers
     *
     * @param status  HTTP status to return
     * @param message error detail message
     * @param request the originating request, used to record the request path
     * @return the assembled error response
     */
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
