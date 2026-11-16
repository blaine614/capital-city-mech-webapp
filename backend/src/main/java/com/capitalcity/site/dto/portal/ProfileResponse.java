package com.capitalcity.site.dto.portal;

import com.capitalcity.site.domain.Employee;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** Everything the portal shows about one employee. Never includes the password hash. */
public record ProfileResponse(
        Long id,
        String email,
        String role,
        boolean active,
        boolean passwordSet,
        String firstName,
        String lastName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String jobTitle,
        String department,
        LocalDate hireDate,
        String supervisor,
        String emergencyName,
        String emergencyRelationship,
        String emergencyPhone,
        Instant lastLoginAt,
        List<LicenseResponse> licenses) {

    public static ProfileResponse from(Employee e, List<LicenseResponse> licenses) {
        return new ProfileResponse(
                e.getId(),
                e.getEmail(),
                e.getRole().name(),
                e.isActive(),
                e.getPasswordHash() != null,
                e.getFirstName(),
                e.getLastName(),
                e.getPhone(),
                e.getAddressLine1(),
                e.getAddressLine2(),
                e.getCity(),
                e.getState(),
                e.getPostalCode(),
                e.getJobTitle(),
                e.getDepartment(),
                e.getHireDate(),
                e.getSupervisor(),
                e.getEmergencyName(),
                e.getEmergencyRelationship(),
                e.getEmergencyPhone(),
                e.getLastLoginAt(),
                licenses);
    }
}
