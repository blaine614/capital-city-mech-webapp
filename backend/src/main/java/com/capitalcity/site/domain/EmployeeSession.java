package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "employee_session")
public class EmployeeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /** SHA-256 of the token. The token itself is never stored. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected EmployeeSession() {}

    public EmployeeSession(Long employeeId, String tokenHash, Instant expiresAt) {
        this.employeeId = employeeId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
}
