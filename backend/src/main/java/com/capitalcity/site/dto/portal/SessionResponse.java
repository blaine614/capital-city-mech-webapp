package com.capitalcity.site.dto.portal;

import java.time.Instant;

/** Returned on sign-in. The browser sends the token back as a Bearer header. */
public record SessionResponse(String token, Instant expiresAt, ProfileResponse profile) {
}
