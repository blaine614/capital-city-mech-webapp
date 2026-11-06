package com.capitalcity.site.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_offering")
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceCategory category;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "service_offering_point",
            joinColumns = @JoinColumn(name = "service_offering_id"))
    @OrderColumn(name = "position")
    @Column(name = "point", nullable = false, length = 240)
    private List<String> points = new ArrayList<>();

    public Long getId() { return id; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
    public ServiceCategory getCategory() { return category; }
    public String getSummary() { return summary; }
    public int getDisplayOrder() { return displayOrder; }
    public boolean isActive() { return active; }
    public List<String> getPoints() { return points; }

    public void setSlug(String slug) { this.slug = slug; }
    public void setName(String name) { this.name = name; }
    public void setCategory(ServiceCategory category) { this.category = category; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    public void setActive(boolean active) { this.active = active; }
    public void setPoints(List<String> points) { this.points = points; }
}
