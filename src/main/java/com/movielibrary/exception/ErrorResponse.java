package com.movielibrary.exception;

/**
 * Standard JSON body returned for error responses
 *
 * @param timestamp ISO-8601 instant at which the error occurred
 * @param status    HTTP status code
 * @param error     HTTP status reason phrase
 * @param message   human-readable error detail
 * @param path      request URI that produced the error
 */
public record ErrorResponse(String timestamp, int status, String error, String message, String path) {
}
