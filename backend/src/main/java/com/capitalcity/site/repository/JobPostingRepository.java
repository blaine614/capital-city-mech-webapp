package com.capitalcity.site.repository;

import com.capitalcity.site.domain.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByActiveTrueOrderByPostedAtDesc();
}
