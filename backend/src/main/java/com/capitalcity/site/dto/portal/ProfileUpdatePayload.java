package com.capitalcity.site.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** The parts of a profile an employee keeps up to date themselves. */
public record ProfileUpdatePayload(

        @NotBlank(message = "Enter your first name.")
        @Size(max = 80)
        String firstName,

        @NotBlank(message = "Enter your last name.")
        @Size(max = 80)
        String lastName,

        @Pattern(regexp = "^$|^[0-9()+\\-.\\s]{10,40}$", message = "Enter a 10-digit phone number.")
        String phone,

        @Size(max = 160) String addressLine1,
        @Size(max = 160) String addressLine2,
        @Size(max = 80) String city,
        @Size(max = 40) String state,
        @Size(max = 20) String postalCode,

        @Size(max = 120) String emergencyName,
        @Size(max = 80) String emergencyRelationship,

        @Pattern(regexp = "^$|^[0-9()+\\-.\\s]{10,40}$", message = "Enter a 10-digit phone number.")
        String emergencyPhone) {
}
