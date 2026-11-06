package com.capitalcity.site.web;

import com.capitalcity.site.dto.ServiceOfferingResponse;
import com.capitalcity.site.service.ServiceCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceCatalogService catalog;

    public ServiceController(ServiceCatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public List<ServiceOfferingResponse> list() {
        return catalog.listActive();
    }
}
