package com.capitalcity.site.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Cors cors, Admin admin) {

    public record Cors(List<String> allowedOrigins) {}

    public record Admin(String token) {}
}
