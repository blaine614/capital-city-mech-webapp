package com.capitalcity.site.web;

import com.capitalcity.site.domain.Employee;
import com.capitalcity.site.dto.SubmissionAck;
import com.capitalcity.site.dto.portal.AdminEmployeePayload;
import com.capitalcity.site.dto.portal.LicensePayload;
import com.capitalcity.site.dto.portal.ProfileResponse;
import com.capitalcity.site.service.EmployeeService;
import com.capitalcity.site.service.PortalAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Office-only: add employees, edit job details and licenses. Every method checks for an admin session. */
@RestController
@RequestMapping("/api/portal/admin/employees")
public class PortalAdminController {

    private final PortalAuthService auth;
    private final EmployeeService employees;

    public PortalAdminController(PortalAuthService auth, EmployeeService employees) {
        this.auth = auth;
        this.employees = employees;
    }

    @GetMapping
    public List<ProfileResponse> list(HttpServletRequest request) {
        auth.requireAdmin(request);
        return employees.listAll();
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody AdminEmployeePayload payload,
                                                  HttpServletRequest request) {
        auth.requireAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employees.create(payload));
    }

    @PutMapping("/{id}")
    public ProfileResponse update(@PathVariable Long id,
                                  @Valid @RequestBody AdminEmployeePayload payload,
                                  HttpServletRequest request) {
        Employee admin = auth.requireAdmin(request);
        return employees.update(id, payload, admin.getId());
    }

    @PostMapping("/{id}/invite")
    public SubmissionAck invite(@PathVariable Long id, HttpServletRequest request) {
        auth.requireAdmin(request);
        employees.resendLink(id);
        return new SubmissionAck(id, "Link sent.");
    }

    @PostMapping("/{id}/licenses")
    public ProfileResponse addLicense(@PathVariable Long id,
                                      @Valid @RequestBody LicensePayload payload,
                                      HttpServletRequest request) {
        auth.requireAdmin(request);
        return employees.addLicense(id, payload);
    }

    @DeleteMapping("/{id}/licenses/{licenseId}")
    public ResponseEntity<ProfileResponse> removeLicense(@PathVariable Long id,
                                                         @PathVariable Long licenseId,
                                                         HttpServletRequest request) {
        auth.requireAdmin(request);
        return ResponseEntity.ok(employees.removeLicense(id, licenseId));
    }
}
