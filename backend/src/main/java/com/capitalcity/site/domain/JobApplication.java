package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "job_application")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(name = "years_experience", nullable = false)
    private int yearsExperience;

    @Column(length = 300)
    private String licenses;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "cover_note", length = 4000)
    private String coverNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.RECEIVED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public JobPosting getJobPosting() { return jobPosting; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getYearsExperience() { return yearsExperience; }
    public String getLicenses() { return licenses; }
    public String getResumeUrl() { return resumeUrl; }
    public String getCoverNote() { return coverNote; }
    public ApplicationStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setJobPosting(JobPosting jobPosting) { this.jobPosting = jobPosting; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }
    public void setLicenses(String licenses) { this.licenses = licenses; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public void setCoverNote(String coverNote) { this.coverNote = coverNote; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
}
