package com.capitalcity.site.dto;

import com.capitalcity.site.domain.ServiceOffering;

import java.util.List;

public record ServiceOfferingResponse(
        Long id,
        String slug,
        String name,
        String category,
        String summary,
        List<String> points) {

    public static ServiceOfferingResponse from(ServiceOffering entity) {
        return new ServiceOfferingResponse(
                entity.getId(),
                entity.getSlug(),
                entity.getName(),
                entity.getCategory().name(),
                entity.getSummary(),
                List.copyOf(entity.getPoints()));
    }
}
