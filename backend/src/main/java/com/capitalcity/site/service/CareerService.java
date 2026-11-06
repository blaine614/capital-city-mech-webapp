package com.capitalcity.site.service;

import com.capitalcity.site.domain.JobApplication;
import com.capitalcity.site.domain.JobPosting;
import com.capitalcity.site.dto.JobApplicationPayload;
import com.capitalcity.site.dto.JobApplicationResponse;
import com.capitalcity.site.dto.JobPostingResponse;
import com.capitalcity.site.exception.NotFoundException;
import com.capitalcity.site.exception.TooManySubmissionsException;
import com.capitalcity.site.repository.JobApplicationRepository;
import com.capitalcity.site.repository.JobPostingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class CareerService {

    private static final Logger log = LoggerFactory.getLogger(CareerService.class);

    private static final int MAX_APPLICATIONS_PER_DAY = 3;

    private final JobPostingRepository postingRepository;
    private final JobApplicationRepository applicationRepository;

    public CareerService(JobPostingRepository postingRepository,
                         JobApplicationRepository applicationRepository) {
        this.postingRepository = postingRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<JobPostingResponse> listOpenPostings() {
        return postingRepository.findByActiveTrueOrderByPostedAtDesc()
                .stream()
                .map(JobPostingResponse::from)
                .toList();
    }

    @Transactional
    public JobApplication apply(JobApplicationPayload payload) {
        JobPosting posting = postingRepository.findById(payload.jobPostingId())
                .filter(JobPosting::isActive)
                .orElseThrow(() -> new NotFoundException("That role is no longer open."));

        Instant dayAgo = Instant.now().minus(Duration.ofDays(1));
        long recent = applicationRepository.countByEmailAndCreatedAtAfter(payload.email(), dayAgo);
        if (recent >= MAX_APPLICATIONS_PER_DAY) {
            throw new TooManySubmissionsException(
                    "You have already applied for several roles today. We have your information.");
        }

        JobApplication entity = new JobApplication();
        entity.setJobPosting(posting);
        entity.setFirstName(payload.firstName().trim());
        entity.setLastName(payload.lastName().trim());
        entity.setEmail(payload.email().trim().toLowerCase());
        entity.setPhone(payload.phone().trim());
        entity.setYearsExperience(payload.yearsExperience());
        entity.setLicenses(trimOrNull(payload.licenses()));
        entity.setResumeUrl(trimOrNull(payload.resumeUrl()));
        entity.setCoverNote(trimOrNull(payload.coverNote()));

        JobApplication saved = applicationRepository.save(entity);
        log.info("Application {} received for posting {}", saved.getId(), posting.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> listApplications(Pageable pageable) {
        return applicationRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(JobApplicationResponse::from);
    }

    private static String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
