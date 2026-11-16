package com.capitalcity.site.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordPayload(

        @NotBlank(message = "This link is missing its code. Open it from the email again.")
        @Size(max = 200)
        String token,

        @NotBlank(message = "Choose a password.")
        @Size(min = 10, max = 72, message = "Use between 10 and 72 characters.")
        String password) {
}
