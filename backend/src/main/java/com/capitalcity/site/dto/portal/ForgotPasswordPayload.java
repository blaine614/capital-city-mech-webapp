package com.capitalcity.site.dto.portal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordPayload(

        @NotBlank(message = "Enter your email address.")
        @Email(message = "That email address does not look right.")
        @Size(max = 180)
        String email) {
}
