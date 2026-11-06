package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "contact_request")
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 40)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false, length = 20)
    private PropertyType propertyType;

    @Column(name = "service_type", nullable = false, length = 80)
    private String serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Urgency urgency;

    @Column(nullable = false, length = 4000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.NEW;

    @Column(name = "source_ip", length = 64)
    private String sourceIp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public PropertyType getPropertyType() { return propertyType; }
    public String getServiceType() { return serviceType; }
    public Urgency getUrgency() { return urgency; }
    public String getMessage() { return message; }
    public RequestStatus getStatus() { return status; }
    public String getSourceIp() { return sourceIp; }
    public Instant getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }
    public void setMessage(String message) { this.message = message; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
}
