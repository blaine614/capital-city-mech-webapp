package com.capitalcity.site.web;

import com.capitalcity.site.domain.Employee;
import com.capitalcity.site.dto.portal.ProfileResponse;
import com.capitalcity.site.dto.portal.ProfileUpdatePayload;
import com.capitalcity.site.service.EmployeeService;
import com.capitalcity.site.service.PortalAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The signed-in employee's own profile. An employee can only ever reach their own record here. */
@RestController
@RequestMapping("/api/portal/me")
public class PortalController {

    private final PortalAuthService auth;
    private final EmployeeService employees;

    public PortalController(PortalAuthService auth, EmployeeService employees) {
        this.auth = auth;
        this.employees = employees;
    }

    @GetMapping
    public ProfileResponse me(HttpServletRequest request) {
        Employee employee = auth.authenticate(request);
        return employees.profile(employee.getId());
    }

    @PutMapping
    public ProfileResponse update(@Valid @RequestBody ProfileUpdatePayload payload,
                                  HttpServletRequest request) {
        Employee employee = auth.authenticate(request);
        return employees.updateOwn(employee.getId(), payload);
    }
}
