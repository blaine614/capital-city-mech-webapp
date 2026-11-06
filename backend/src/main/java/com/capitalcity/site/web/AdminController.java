package com.capitalcity.site.web;

import com.capitalcity.site.config.AppProperties;
import com.capitalcity.site.dto.ContactRequestResponse;
import com.capitalcity.site.dto.JobApplicationResponse;
import com.capitalcity.site.exception.UnauthorizedException;
import com.capitalcity.site.service.CareerService;
import com.capitalcity.site.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Read-only office view of what came in through the site.
 * Guarded by a shared token header; swap for Spring Security when
 * the office needs real accounts.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ContactService contactService;
    private final CareerService careerService;
    private final AppProperties properties;

    public AdminController(ContactService contactService,
                           CareerService careerService,
                           AppProperties properties) {
        this.contactService = contactService;
        this.careerService = careerService;
        this.properties = properties;
    }

    @GetMapping("/contact-requests")
    public Page<ContactRequestResponse> contactRequests(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        requireToken(token);
        return contactService.list(PageRequest.of(page, Math.min(size, 100)));
    }

    @GetMapping("/applications")
    public Page<JobApplicationResponse> applications(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        requireToken(token);
        return careerService.listApplications(PageRequest.of(page, Math.min(size, 100)));
    }

    private void requireToken(String token) {
        String expected = properties.admin().token();
        if (token == null || !token.equals(expected)) {
            throw new UnauthorizedException("Admin token missing or wrong.");
        }
    }
}
