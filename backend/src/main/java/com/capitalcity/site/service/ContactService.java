package com.capitalcity.site.service;

import com.capitalcity.site.domain.ContactRequest;
import com.capitalcity.site.dto.ContactRequestPayload;
import com.capitalcity.site.dto.ContactRequestResponse;
import com.capitalcity.site.exception.TooManySubmissionsException;
import com.capitalcity.site.repository.ContactRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    /** Same email cannot file more than this many requests per hour. */
    private static final int MAX_PER_HOUR = 5;

    private final ContactRequestRepository repository;

    public ContactService(ContactRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContactRequest submit(ContactRequestPayload payload, String sourceIp) {
        Instant hourAgo = Instant.now().minus(Duration.ofHours(1));
        long recent = repository.countByEmailAndCreatedAtAfter(payload.email(), hourAgo);
        if (recent >= MAX_PER_HOUR) {
            throw new TooManySubmissionsException(
                    "We already have several requests from this address. Call the shop instead.");
        }

        ContactRequest entity = new ContactRequest();
        entity.setName(payload.name().trim());
        entity.setEmail(payload.email().trim().toLowerCase());
        entity.setPhone(payload.phone().trim());
        entity.setPropertyType(payload.propertyType());
        entity.setServiceType(payload.serviceType().trim());
        entity.setUrgency(payload.urgency());
        entity.setMessage(payload.message().trim());
        entity.setSourceIp(sourceIp);

        ContactRequest saved = repository.save(entity);
        log.info("Contact request {} received: {} / {}",
                saved.getId(), saved.getUrgency(), saved.getServiceType());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<ContactRequestResponse> list(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable)
                .map(ContactRequestResponse::from);
    }
}
