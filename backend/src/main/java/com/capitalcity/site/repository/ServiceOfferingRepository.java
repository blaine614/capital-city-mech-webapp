package com.capitalcity.site.repository;

import com.capitalcity.site.domain.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    List<ServiceOffering> findByActiveTrueOrderByDisplayOrderAsc();
}
