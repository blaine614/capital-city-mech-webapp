package com.capitalcity.site.dto;

import com.capitalcity.site.domain.JobPosting;

import java.time.Instant;
import java.util.List;

public record JobPostingResponse(
        Long id,
        String title,
        String department,
        String location,
        String employmentType,
        String description,
        List<String> requirements,
        Instant postedAt) {

    public static JobPostingResponse from(JobPosting entity) {
        return new JobPostingResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDepartment(),
                entity.getLocation(),
                entity.getEmploymentType(),
                entity.getDescription(),
                List.copyOf(entity.getRequirements()),
                entity.getPostedAt());
    }
}
