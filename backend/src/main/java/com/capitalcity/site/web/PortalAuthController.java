package com.capitalcity.site.web;

import com.capitalcity.site.dto.SubmissionAck;
import com.capitalcity.site.dto.portal.ForgotPasswordPayload;
import com.capitalcity.site.dto.portal.LoginPayload;
import com.capitalcity.site.dto.portal.SessionResponse;
import com.capitalcity.site.dto.portal.SetPasswordPayload;
import com.capitalcity.site.exception.UnauthorizedException;
import com.capitalcity.site.service.EmployeeService;
import com.capitalcity.site.service.PortalAuthService;
import com.capitalcity.site.service.PortalAuthService.SessionGrant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Signing in and out of the employee portal, and setting a password from an emailed link. */
@RestController
@RequestMapping("/api/portal/auth")
public class PortalAuthController {

    private final PortalAuthService auth;
    private final EmployeeService employees;

    public PortalAuthController(PortalAuthService auth, EmployeeService employees) {
        this.auth = auth;
        this.employees = employees;
    }

    @PostMapping("/login")
    public SessionResponse login(@Valid @RequestBody LoginPayload payload) {
        SessionGrant grant = auth.login(payload.email(), payload.password())
                .orElseThrow(() -> new UnauthorizedException(
                        "That email and password do not match. After 5 misses the account "
                                + "locks for 15 minutes."));
        return new SessionResponse(
                grant.token(), grant.expiresAt(), employees.profile(grant.employee().getId()));
    }

    @PostMapping("/logout")
    public SubmissionAck logout(HttpServletRequest request) {
        auth.logout(request);
        return new SubmissionAck(null, "Signed out.");
    }

    /** Always answers the same way, so it cannot be used to check who works here. */
    @PostMapping("/forgot")
    public SubmissionAck forgot(@Valid @RequestBody ForgotPasswordPayload payload) {
        auth.requestPasswordLink(payload.email());
        return new SubmissionAck(null,
                "If that address is on file, a link to set your password is on its way.");
    }

    @PostMapping("/set-password")
    public SubmissionAck setPassword(@Valid @RequestBody SetPasswordPayload payload) {
        auth.setPassword(payload.token(), payload.password());
        return new SubmissionAck(null, "Password saved. You can sign in now.");
    }
}
