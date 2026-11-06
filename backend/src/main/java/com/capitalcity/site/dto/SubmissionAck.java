package com.capitalcity.site.dto;

/** What the browser gets back after a successful submission. */
public record SubmissionAck(Long id, String message) {
}
