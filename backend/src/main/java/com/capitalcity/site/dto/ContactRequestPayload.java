package com.capitalcity.site.dto;

import com.capitalcity.site.domain.PropertyType;
import com.capitalcity.site.domain.Urgency;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactRequestPayload(

        @NotBlank(message = "Enter the name we should ask for.")
        @Size(max = 120)
        String name,

        @NotBlank(message = "Enter an email address we can reply to.")
        @Email(message = "That email address does not look right.")
        @Size(max = 180)
        String email,

        @NotBlank(message = "Enter a phone number.")
        @Pattern(regexp = "^[0-9()+\\-.\\s]{10,40}$",
                 message = "Enter a 10-digit phone number.")
        String phone,

        @NotNull(message = "Choose a property type.")
        PropertyType propertyType,

        @NotBlank(message = "Pick the closest match.")
        @Size(max = 80)
        String serviceType,

        @NotNull(message = "Tell us how soon you need someone.")
        Urgency urgency,

        @NotBlank(message = "Tell us about the job.")
        @Size(min = 10, max = 4000,
              message = "Give us at least a sentence about the job.")
        String message) {
}
