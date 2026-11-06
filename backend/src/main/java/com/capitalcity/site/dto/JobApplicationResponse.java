package com.capitalcity.site.dto;

import com.capitalcity.site.domain.JobApplication;

import java.time.Instant;

public record JobApplicationResponse(
        Long id,
        Long jobPostingId,
        String jobTitle,
        String firstName,
        String lastName,
        String email,
        String phone,
        int yearsExperience,
        String licenses,
        String resumeUrl,
        String coverNote,
        String status,
        Instant createdAt) {

    public static JobApplicationResponse from(JobApplication entity) {
        return new JobApplicationResponse(
                entity.getId(),
                entity.getJobPosting().getId(),
                entity.getJobPosting().getTitle(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getYearsExperience(),
                entity.getLicenses(),
                entity.getResumeUrl(),
                entity.getCoverNote(),
                entity.getStatus().name(),
                entity.getCreatedAt());
    }
}
