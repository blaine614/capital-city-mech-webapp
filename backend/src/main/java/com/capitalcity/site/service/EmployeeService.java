package com.capitalcity.site.service;

import com.capitalcity.site.domain.Employee;
import com.capitalcity.site.domain.EmployeeLicense;
import com.capitalcity.site.domain.EmployeeRole;
import com.capitalcity.site.dto.portal.AdminEmployeePayload;
import com.capitalcity.site.dto.portal.LicensePayload;
import com.capitalcity.site.dto.portal.LicenseResponse;
import com.capitalcity.site.dto.portal.ProfileResponse;
import com.capitalcity.site.dto.portal.ProfileUpdatePayload;
import com.capitalcity.site.exception.BadRequestException;
import com.capitalcity.site.exception.NotFoundException;
import com.capitalcity.site.repository.EmployeeLicenseRepository;
import com.capitalcity.site.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/** Employee profiles: what an employee edits themselves, and what the office manages. */
@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employees;
    private final EmployeeLicenseRepository licenses;
    private final PortalAuthService auth;

    public EmployeeService(EmployeeRepository employees,
                           EmployeeLicenseRepository licenses,
                           PortalAuthService auth) {
        this.employees = employees;
        this.licenses = licenses;
        this.auth = auth;
    }

    // ---------- The employee's own profile ----------

    @Transactional(readOnly = true)
    public ProfileResponse profile(Long employeeId) {
        return toResponse(load(employeeId));
    }

    /** Contact details and emergency contact. Job details and licenses stay with the office. */
    @Transactional
    public ProfileResponse updateOwn(Long employeeId, ProfileUpdatePayload payload) {
        Employee e = load(employeeId);
        e.setFirstName(payload.firstName().trim());
        e.setLastName(payload.lastName().trim());
        e.setPhone(trimOrNull(payload.phone()));
        e.setAddressLine1(trimOrNull(payload.addressLine1()));
        e.setAddressLine2(trimOrNull(payload.addressLine2()));
        e.setCity(trimOrNull(payload.city()));
        e.setState(trimOrNull(payload.state()));
        e.setPostalCode(trimOrNull(payload.postalCode()));
        e.setEmergencyName(trimOrNull(payload.emergencyName()));
        e.setEmergencyRelationship(trimOrNull(payload.emergencyRelationship()));
        e.setEmergencyPhone(trimOrNull(payload.emergencyPhone()));
        return toResponse(e);
    }

    // ---------- Office (admin) ----------

    @Transactional(readOnly = true)
    public List<ProfileResponse> listAll() {
        return employees.findAllByOrderByLastNameAscFirstNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Adds an employee and emails them a link to choose their password. */
    @Transactional
    public ProfileResponse create(AdminEmployeePayload payload) {
        String email = PortalAuthService.normalize(payload.email());
        if (employees.existsByEmail(email)) {
            throw emailTaken();
        }

        Employee e = new Employee();
        e.setEmail(email);
        applyOfficeFields(e, payload);
        e.setRole(roleOf(payload));
        e.setActive(true);
        Employee saved = employees.save(e);

        auth.sendPasswordLink(saved);
        log.info("Portal account {} created and invited", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public ProfileResponse update(Long employeeId, AdminEmployeePayload payload, Long actingAdminId) {
        Employee e = load(employeeId);

        String email = PortalAuthService.normalize(payload.email());
        if (!email.equals(e.getEmail())) {
            if (employees.existsByEmail(email)) {
                throw emailTaken();
            }
            e.setEmail(email);
        }

        EmployeeRole role = roleOf(payload);
        boolean active = payload.active() == null || payload.active();
        if (employeeId.equals(actingAdminId) && (role != EmployeeRole.ADMIN || !active)) {
            throw new BadRequestException(
                    "You cannot remove your own office access. Have another office admin do it.");
        }

        applyOfficeFields(e, payload);
        e.setRole(role);
        if (e.isActive() && !active) {
            auth.signOutEverywhere(e.getId());
        }
        e.setActive(active);
        return toResponse(e);
    }

    /** Sends a fresh invite, or a reset link if they already have a password. */
    @Transactional
    public void resendLink(Long employeeId) {
        Employee e = load(employeeId);
        if (!e.isActive()) {
            throw new BadRequestException("Reactivate this employee before sending a link.");
        }
        auth.sendPasswordLink(e);
    }

    @Transactional
    public ProfileResponse addLicense(Long employeeId, LicensePayload payload) {
        Employee e = load(employeeId);
        EmployeeLicense license = new EmployeeLicense();
        license.setEmployeeId(e.getId());
        license.setName(payload.name().trim());
        license.setLicenseNumber(trimOrNull(payload.licenseNumber()));
        license.setIssuer(trimOrNull(payload.issuer()));
        license.setExpiresOn(payload.expiresOn());
        licenses.save(license);
        return toResponse(e);
    }

    @Transactional
    public ProfileResponse removeLicense(Long employeeId, Long licenseId) {
        Employee e = load(employeeId);
        EmployeeLicense license = licenses.findByIdAndEmployeeId(licenseId, employeeId)
                .orElseThrow(() -> new NotFoundException("That license is no longer on file."));
        licenses.delete(license);
        licenses.flush();
        return toResponse(e);
    }

    // ---------- Helpers ----------

    private Employee load(Long employeeId) {
        return employees.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("That employee is no longer on file."));
    }

    private ProfileResponse toResponse(Employee e) {
        List<LicenseResponse> held = licenses.findByEmployeeIdOrderByNameAsc(e.getId())
                .stream()
                .map(LicenseResponse::from)
                .toList();
        return ProfileResponse.from(e, held);
    }

    private static void applyOfficeFields(Employee e, AdminEmployeePayload payload) {
        e.setFirstName(payload.firstName().trim());
        e.setLastName(payload.lastName().trim());
        e.setJobTitle(trimOrNull(payload.jobTitle()));
        e.setDepartment(trimOrNull(payload.department()));
        e.setHireDate(payload.hireDate());
        e.setSupervisor(trimOrNull(payload.supervisor()));
    }

    private static EmployeeRole roleOf(AdminEmployeePayload payload) {
        return "ADMIN".equals(payload.role()) ? EmployeeRole.ADMIN : EmployeeRole.EMPLOYEE;
    }

    private static BadRequestException emailTaken() {
        return new BadRequestException("Check the highlighted fields and try again.",
                Map.of("email", "Another employee already uses that email address."));
    }

    private static String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
