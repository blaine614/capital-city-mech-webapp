package com.capitalcity.site.web;

import com.capitalcity.site.domain.ContactRequest;
import com.capitalcity.site.dto.ContactRequestPayload;
import com.capitalcity.site.dto.SubmissionAck;
import com.capitalcity.site.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<SubmissionAck> submit(@Valid @RequestBody ContactRequestPayload payload,
                                                HttpServletRequest request) {
        ContactRequest saved = contactService.submit(payload, clientIp(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubmissionAck(saved.getId(), "Request received."));
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
