package com.capitalcity.site.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Cors cors, Admin admin, Mail mail, Portal portal) {

    public record Cors(List<String> allowedOrigins) {}

    public record Admin(String token) {}

    /**
     * @param from              address the website sends from
     * @param serviceRequestsTo inbox for Contact page service requests
     * @param applicationsTo    inbox for Careers page job applications
     */
    public record Mail(String from, String serviceRequestsTo, String applicationsTo) {}

    /**
     * @param siteUrl    public address of the website, used in emailed links
     * @param adminEmail first office account, created on startup if missing
     */
    public record Portal(String siteUrl, String adminEmail) {}
}
