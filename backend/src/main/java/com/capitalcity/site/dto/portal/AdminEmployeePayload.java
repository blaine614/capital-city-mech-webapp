package com.capitalcity.site.dto.portal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** What the office sets when adding or editing an employee. */
public record AdminEmployeePayload(

        @NotBlank(message = "Enter the employee's email address.")
        @Email(message = "That email address does not look right.")
        @Size(max = 180)
        String email,

        @NotBlank(message = "Enter a first name.")
        @Size(max = 80)
        String firstName,

        @NotBlank(message = "Enter a last name.")
        @Size(max = 80)
        String lastName,

        @Size(max = 120) String jobTitle,
        @Size(max = 80) String department,
        LocalDate hireDate,
        @Size(max = 120) String supervisor,

        /** EMPLOYEE or ADMIN. Defaults to EMPLOYEE. */
        @Pattern(regexp = "EMPLOYEE|ADMIN", message = "Choose Employee or Office admin.")
        String role,

        /** Defaults to true. */
        Boolean active) {
}
