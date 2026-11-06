package com.capitalcity.site.dto;

import com.capitalcity.site.domain.ContactRequest;

import java.time.Instant;

public record ContactRequestResponse(
        Long id,
        String name,
        String email,
        String phone,
        String propertyType,
        String serviceType,
        String urgency,
        String message,
        String status,
        Instant createdAt) {

    public static ContactRequestResponse from(ContactRequest entity) {
        return new ContactRequestResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getPropertyType().name(),
                entity.getServiceType(),
                entity.getUrgency().name(),
                entity.getMessage(),
                entity.getStatus().name(),
                entity.getCreatedAt());
    }
}
