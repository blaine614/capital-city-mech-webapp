package com.capitalcity.site.repository;

import com.capitalcity.site.domain.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByEmailAndCreatedAtAfter(String email, Instant since);
}
