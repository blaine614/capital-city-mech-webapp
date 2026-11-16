package com.capitalcity.site.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginPayload(

        @NotBlank(message = "Enter your email address.")
        @Size(max = 180)
        String email,

        @NotBlank(message = "Enter your password.")
        @Size(max = 200)
        String password) {
}
