package com.capitalcity.site.repository;

import com.capitalcity.site.domain.EmployeeSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeSessionRepository extends JpaRepository<EmployeeSession, Long> {

    Optional<EmployeeSession> findByTokenHash(String tokenHash);

    void deleteByEmployeeId(Long employeeId);

    void deleteByTokenHash(String tokenHash);
}
