package com.capitalcity.site.repository;

import com.capitalcity.site.domain.EmployeeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface EmployeeTokenRepository extends JpaRepository<EmployeeToken, Long> {

    Optional<EmployeeToken> findByTokenHash(String tokenHash);

    long countByEmployeeIdAndCreatedAtAfter(Long employeeId, Instant since);

    void deleteByEmployeeId(Long employeeId);
}
