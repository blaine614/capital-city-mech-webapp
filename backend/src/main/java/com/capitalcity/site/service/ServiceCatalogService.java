package com.capitalcity.site.service;

import com.capitalcity.site.dto.ServiceOfferingResponse;
import com.capitalcity.site.repository.ServiceOfferingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceCatalogService {

    private final ServiceOfferingRepository repository;

    public ServiceCatalogService(ServiceOfferingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> listActive() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(ServiceOfferingResponse::from)
                .toList();
    }
}
