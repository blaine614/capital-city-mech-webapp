package com.capitalcity.site.dto.portal;

import com.capitalcity.site.domain.EmployeeLicense;

import java.time.LocalDate;

public record LicenseResponse(
        Long id,
        String name,
        String licenseNumber,
        String issuer,
        LocalDate expiresOn) {

    public static LicenseResponse from(EmployeeLicense license) {
        return new LicenseResponse(
                license.getId(),
                license.getName(),
                license.getLicenseNumber(),
                license.getIssuer(),
                license.getExpiresOn());
    }
}
