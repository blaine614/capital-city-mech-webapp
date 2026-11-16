package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee_license")
public class EmployeeLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "license_number", length = 80)
    private String licenseNumber;

    @Column(length = 120)
    private String issuer;

    @Column(name = "expires_on")
    private LocalDate expiresOn;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getIssuer() { return issuer; }
    public LocalDate getExpiresOn() { return expiresOn; }
    public Instant getCreatedAt() { return createdAt; }

    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setName(String name) { this.name = name; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public void setExpiresOn(LocalDate expiresOn) { this.expiresOn = expiresOn; }
}
