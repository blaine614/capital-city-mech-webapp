package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_posting")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(name = "employment_type", nullable = false, length = 60)
    private String employmentType;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "posted_at", nullable = false, updatable = false)
    private Instant postedAt = Instant.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "job_posting_requirement",
            joinColumns = @JoinColumn(name = "job_posting_id"))
    @OrderColumn(name = "position")
    @Column(name = "requirement", nullable = false, length = 240)
    private List<String> requirements = new ArrayList<>();

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getLocation() { return location; }
    public String getEmploymentType() { return employmentType; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public Instant getPostedAt() { return postedAt; }
    public List<String> getRequirements() { return requirements; }

    public void setTitle(String title) { this.title = title; }
    public void setDepartment(String department) { this.department = department; }
    public void setLocation(String location) { this.location = location; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { this.active = active; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }
}
