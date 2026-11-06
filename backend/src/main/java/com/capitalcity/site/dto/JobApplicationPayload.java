package com.capitalcity.site.dto;

import jakarta.validation.constraints.*;

public record JobApplicationPayload(

        @NotNull(message = "Pick the role you want.")
        Long jobPostingId,

        @NotBlank(message = "Enter your first name.")
        @Size(max = 80)
        String firstName,

        @NotBlank(message = "Enter your last name.")
        @Size(max = 80)
        String lastName,

        @NotBlank(message = "Enter an email address we can reach you at.")
        @Email(message = "That email address does not look right.")
        @Size(max = 180)
        String email,

        @NotBlank(message = "Enter a phone number.")
        @Pattern(regexp = "^[0-9()+\\-.\\s]{10,40}$",
                 message = "Enter a 10-digit phone number.")
        String phone,

        @NotNull(message = "Enter years in the trade — 0 is fine.")
        @Min(value = 0, message = "Years cannot be negative.")
        @Max(value = 60, message = "Enter 60 years or fewer.")
        Integer yearsExperience,

        @Size(max = 300)
        String licenses,

        @Size(max = 500)
        String resumeUrl,

        @Size(max = 4000)
        String coverNote) {
}
