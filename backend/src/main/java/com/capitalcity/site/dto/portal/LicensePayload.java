package com.capitalcity.site.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LicensePayload(

        @NotBlank(message = "Enter the license or certification name.")
        @Size(max = 160)
        String name,

        @Size(max = 80) String licenseNumber,
        @Size(max = 120) String issuer,
        LocalDate expiresOn) {
}
