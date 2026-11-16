package com.capitalcity.site.service;

import com.capitalcity.site.config.AppProperties;
import com.capitalcity.site.domain.ContactRequest;
import com.capitalcity.site.domain.Employee;
import com.capitalcity.site.domain.JobApplication;
import com.capitalcity.site.domain.Urgency;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Writes the emails the site sends and hands them to {@link Mailer}. */
@Service
public class NotificationService {

    private static final DateTimeFormatter SHOP_TIME = DateTimeFormatter
            .ofPattern("EEE MMM d, yyyy 'at' h:mm a z", Locale.US)
            .withZone(ZoneId.of("America/New_York"));

    private final Mailer mailer;
    private final AppProperties properties;

    public NotificationService(Mailer mailer, AppProperties properties) {
        this.mailer = mailer;
        this.properties = properties;
    }

    /** Contact page service request: goes to the service inbox. */
    public void contactRequestReceived(ContactRequest request) {
        String flag = request.getUrgency() == Urgency.EMERGENCY ? "EMERGENCY: " : "";
        String subject = flag + "Service request #" + request.getId()
                + " - " + request.getServiceType() + " - " + request.getName();

        String body = "New service request from the website.\n\n"
                + line("Name", request.getName())
                + line("Phone", request.getPhone())
                + line("Email", request.getEmail())
                + line("Property", label(request.getPropertyType().name()))
                + line("Needs", request.getServiceType())
                + line("How soon", label(request.getUrgency().name()))
                + line("Received", when(request.getCreatedAt()))
                + "\nDetails:\n" + request.getMessage() + "\n\n"
                + "Reply to this email to answer " + request.getName() + " directly.\n";

        mailer.send(properties.mail().serviceRequestsTo(), request.getEmail(), subject, body);
    }

    /** Careers page application: goes to the hiring inbox. */
    public void applicationReceived(JobApplication application, String postingTitle) {
        String fullName = application.getFirstName() + " " + application.getLastName();
        String subject = "Job application #" + application.getId()
                + " - " + postingTitle + " - " + fullName;

        String body = "New job application from the website.\n\n"
                + line("Role", postingTitle)
                + line("Name", fullName)
                + line("Phone", application.getPhone())
                + line("Email", application.getEmail())
                + line("Experience", application.getYearsExperience() + " years")
                + line("Licenses", orDash(application.getLicenses()))
                + line("Resume", orDash(application.getResumeUrl()))
                + line("Received", when(application.getCreatedAt()))
                + "\nNote from the applicant:\n" + orDash(application.getCoverNote()) + "\n\n"
                + "Reply to this email to answer " + fullName + " directly.\n";

        mailer.send(properties.mail().applicationsTo(), application.getEmail(), subject, body);
    }

    /** Invite for a new account, or a reset link for an existing one. */
    public void portalPasswordLink(Employee employee, String rawToken, boolean invite, long validHours) {
        String link = siteUrl() + "/portal/set-password?token=" + rawToken;
        String subject = invite
                ? "Set up your Capital City Mechanical employee portal login"
                : "Reset your Capital City Mechanical employee portal password";

        String intro = invite
                ? "The office has set up an employee portal account for you. "
                        + "Use the link below to choose your password:"
                : "Use the link below to choose a new employee portal password:";

        String body = "Hi " + employee.getFirstName() + ",\n\n"
                + intro + "\n\n"
                + link + "\n\n"
                + "The link works once and expires in " + describe(validHours) + ". "
                + "After that, sign in at " + siteUrl() + "/portal with this email address.\n\n"
                + "If you were not expecting this, you can ignore it.\n\n"
                + "Capital City Mechanical\n";

        mailer.send(employee.getEmail(), null, subject, body);
    }

    private String siteUrl() {
        String url = properties.portal().siteUrl();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String describe(long hours) {
        return hours >= 48 ? (hours / 24) + " days" : hours + " hours";
    }

    private static String line(String label, String value) {
        return String.format("%-11s %s%n", label + ":", value);
    }

    private static String when(Instant instant) {
        return SHOP_TIME.format(instant);
    }

    /** RESIDENTIAL -> Residential */
    private static String label(String enumName) {
        String lower = enumName.toLowerCase(Locale.US).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String orDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
