package com.capitalcity.site.exception;

public class TooManySubmissionsException extends RuntimeException {
    public TooManySubmissionsException(String message) {
        super(message);
    }
}
