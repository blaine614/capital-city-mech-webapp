package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    /** Null until the employee sets a password from their invite link. */
    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_role", nullable = false, length = 20)
    private EmployeeRole role = EmployeeRole.EMPLOYEE;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(length = 40)
    private String phone;

    @Column(name = "address_line1", length = 160)
    private String addressLine1;

    @Column(name = "address_line2", length = 160)
    private String addressLine2;

    @Column(length = 80)
    private String city;

    @Column(length = 40)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "job_title", length = 120)
    private String jobTitle;

    @Column(length = 80)
    private String department;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(length = 120)
    private String supervisor;

    @Column(name = "emergency_name", length = 120)
    private String emergencyName;

    @Column(name = "emergency_relationship", length = 80)
    private String emergencyRelationship;

    @Column(name = "emergency_phone", length = 40)
    private String emergencyPhone;

    @Column(name = "failed_logins", nullable = false)
    private int failedLogins;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public EmployeeRole getRole() { return role; }
    public boolean isActive() { return active; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public LocalDate getHireDate() { return hireDate; }
    public String getSupervisor() { return supervisor; }
    public String getEmergencyName() { return emergencyName; }
    public String getEmergencyRelationship() { return emergencyRelationship; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public int getFailedLogins() { return failedLogins; }
    public Instant getLockedUntil() { return lockedUntil; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public Instant getCreatedAt() { return createdAt; }

    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(EmployeeRole role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setDepartment(String department) { this.department = department; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }
    public void setEmergencyRelationship(String emergencyRelationship) { this.emergencyRelationship = emergencyRelationship; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    public void setFailedLogins(int failedLogins) { this.failedLogins = failedLogins; }
    public void setLockedUntil(Instant lockedUntil) { this.lockedUntil = lockedUntil; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
