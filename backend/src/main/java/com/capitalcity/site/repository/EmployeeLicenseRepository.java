package com.capitalcity.site.repository;

import com.capitalcity.site.domain.EmployeeLicense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeLicenseRepository extends JpaRepository<EmployeeLicense, Long> {

    List<EmployeeLicense> findByEmployeeIdOrderByNameAsc(Long employeeId);

    Optional<EmployeeLicense> findByIdAndEmployeeId(Long id, Long employeeId);
}
