package com.capitalcity.site.exception;

import java.util.Map;

/**
 * Error shape the frontend expects: a sentence to show the person, plus
 * field-level messages it can attach to individual inputs.
 */
public record ApiError(String message, Map<String, String> fieldErrors) {

    public static ApiError of(String message) {
        return new ApiError(message, Map.of());
    }
}
