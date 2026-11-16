package com.capitalcity.site.service;

import com.capitalcity.site.config.AppProperties;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Sends plain-text email over SMTP in the background. A failure is logged and
 * never reaches the visitor: the request is already saved in the database.
 */
@Component
public class Mailer {

    private static final Logger log = LoggerFactory.getLogger(Mailer.class);

    private static final String FROM_NAME = "Capital City Mechanical";

    private final ObjectProvider<JavaMailSender> senderProvider;
    private final AppProperties properties;
    private final String smtpHost;

    public Mailer(ObjectProvider<JavaMailSender> senderProvider,
                  AppProperties properties,
                  @Value("${spring.mail.host:}") String smtpHost) {
        this.senderProvider = senderProvider;
        this.properties = properties;
        this.smtpHost = smtpHost == null ? "" : smtpHost.trim();
    }

    /**
     * @param replyTo optional; when set, hitting Reply answers this address
     */
    @Async
    public void send(String to, String replyTo, String subject, String body) {
        String cleanSubject = singleLine(subject);
        JavaMailSender sender = smtpHost.isEmpty() ? null : senderProvider.getIfAvailable();
        if (sender == null) {
            log.warn("SMTP_HOST is not set, so this email was NOT sent.\nTo: {}\nSubject: {}\n\n{}",
                    to, cleanSubject, body);
            return;
        }

        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(properties.mail().from(), FROM_NAME);
            helper.setTo(to);
            if (replyTo != null && !replyTo.isBlank()) {
                try {
                    helper.setReplyTo(singleLine(replyTo));
                } catch (Exception ex) {
                    log.warn("Skipping unusable reply-to address on \"{}\"", cleanSubject);
                }
            }
            helper.setSubject(cleanSubject);
            helper.setText(body, false);
            sender.send(message);
            log.info("Email sent to {}: {}", to, cleanSubject);
        } catch (Exception ex) {
            log.error("Email to {} failed: {}", to, cleanSubject, ex);
        }
    }

    /** Keeps visitor-typed text from adding header lines. */
    private static String singleLine(String value) {
        return value == null ? "" : value.replaceAll("[\\r\\n]+", " ").trim();
    }
}
