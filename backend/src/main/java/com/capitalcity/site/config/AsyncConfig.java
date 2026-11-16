package com.capitalcity.site.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Lets email go out in the background so a slow mail server never holds up a form. */
@Configuration
@EnableAsync
public class AsyncConfig {
}
