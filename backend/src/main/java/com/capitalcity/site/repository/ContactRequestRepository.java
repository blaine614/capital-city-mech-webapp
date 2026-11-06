package com.capitalcity.site.repository;

import com.capitalcity.site.domain.ContactRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    Page<ContactRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByEmailAndCreatedAtAfter(String email, Instant since);
}
