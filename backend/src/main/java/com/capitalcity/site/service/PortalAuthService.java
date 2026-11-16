package com.capitalcity.site.service;

import com.capitalcity.site.config.AppProperties;
import com.capitalcity.site.domain.Employee;
import com.capitalcity.site.domain.EmployeeRole;
import com.capitalcity.site.domain.EmployeeSession;
import com.capitalcity.site.domain.EmployeeToken;
import com.capitalcity.site.exception.BadRequestException;
import com.capitalcity.site.exception.ForbiddenException;
import com.capitalcity.site.exception.UnauthorizedException;
import com.capitalcity.site.repository.EmployeeRepository;
import com.capitalcity.site.repository.EmployeeSessionRepository;
import com.capitalcity.site.repository.EmployeeTokenRepository;
import com.capitalcity.site.security.Tokens;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Sign-in for the employee portal.
 *
 * Passwords are stored as BCrypt hashes. A session is a random token the
 * browser sends as "Authorization: Bearer ..."; only its SHA-256 is stored,
 * so a copy of the database cannot be used to sign in as anyone.
 */
@Service
public class PortalAuthService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PortalAuthService.class);

    private static final Duration SESSION_TTL = Duration.ofDays(7);
    private static final Duration INVITE_TTL = Duration.ofDays(7);
    private static final Duration RESET_TTL = Duration.ofHours(2);

    private static final int MAX_FAILED_LOGINS = 5;
    private static final Duration LOCKOUT = Duration.ofMinutes(15);
    private static final int MAX_LINKS_PER_HOUR = 3;

    /** BCrypt only reads the first 72 bytes of a password. */
    private static final int MAX_PASSWORD_BYTES = 72;

    private final EmployeeRepository employees;
    private final EmployeeSessionRepository sessions;
    private final EmployeeTokenRepository tokens;
    private final NotificationService notifications;
    private final AppProperties properties;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    /** Checked when the account does not exist, so a miss takes as long as a hit. */
    private final String decoyHash = encoder.encode("no-such-account");

    public record SessionGrant(String token, Instant expiresAt, Employee employee) {}

    public PortalAuthService(EmployeeRepository employees,
                             EmployeeSessionRepository sessions,
                             EmployeeTokenRepository tokens,
                             NotificationService notifications,
                             AppProperties properties) {
        this.employees = employees;
        this.sessions = sessions;
        this.tokens = tokens;
        this.notifications = notifications;
        this.properties = properties;
    }

    /** Creates the first office account so someone can sign in and invite everyone else. */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String configured = properties.portal().adminEmail();
        if (configured == null || configured.isBlank()) {
            if (employees.count() == 0) {
                log.warn("PORTAL_ADMIN_EMAIL is not set and there are no portal accounts yet. "
                        + "Set it to the office manager's email to create the first login.");
            }
            return;
        }
        String email = normalize(configured);
        if (employees.existsByEmail(email)) return;

        Employee admin = new Employee();
        admin.setEmail(email);
        admin.setFirstName("Office");
        admin.setLastName("Admin");
        admin.setRole(EmployeeRole.ADMIN);
        employees.save(admin);
        log.info("Created the first portal office account for {}. "
                + "Use \"First time here or forgot your password?\" on /portal to set its password.", email);
    }

    /**
     * Empty means "email or password is wrong". Nothing is thrown for a bad
     * password, because the failed-attempt count has to be saved.
     */
    @Transactional
    public Optional<SessionGrant> login(String email, String password) {
        Instant now = Instant.now();
        boolean tooLong = password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES;

        Employee employee = employees.findByEmail(normalize(email)).orElse(null);
        if (employee == null || !employee.isActive() || employee.getPasswordHash() == null || tooLong) {
            encoder.matches(tooLong ? "x" : password, decoyHash);
            return Optional.empty();
        }
        if (employee.getLockedUntil() != null && employee.getLockedUntil().isAfter(now)) {
            return Optional.empty();
        }
        if (!encoder.matches(password, employee.getPasswordHash())) {
            int failures = employee.getFailedLogins() + 1;
            if (failures >= MAX_FAILED_LOGINS) {
                employee.setLockedUntil(now.plus(LOCKOUT));
                failures = 0;
                log.warn("Portal account {} locked for {} minutes after repeated failed sign-ins",
                        employee.getId(), LOCKOUT.toMinutes());
            }
            employee.setFailedLogins(failures);
            return Optional.empty();
        }

        employee.setFailedLogins(0);
        employee.setLockedUntil(null);
        employee.setLastLoginAt(now);

        String raw = Tokens.random();
        Instant expiresAt = now.plus(SESSION_TTL);
        sessions.save(new EmployeeSession(employee.getId(), Tokens.sha256(raw), expiresAt));
        return Optional.of(new SessionGrant(raw, expiresAt, employee));
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String raw = bearerToken(request);
        if (raw != null) sessions.deleteByTokenHash(Tokens.sha256(raw));
    }

    /** The signed-in employee for this request, or a 401. */
    @Transactional(readOnly = true)
    public Employee authenticate(HttpServletRequest request) {
        String raw = bearerToken(request);
        if (raw == null) throw new UnauthorizedException("Sign in to continue.");

        EmployeeSession session = sessions.findByTokenHash(Tokens.sha256(raw))
                .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new UnauthorizedException("Your session has ended. Sign in again."));

        return employees.findById(session.getEmployeeId())
                .filter(Employee::isActive)
                .orElseThrow(() -> new UnauthorizedException("Your session has ended. Sign in again."));
    }

    /** The signed-in office admin for this request, or a 401/403. */
    @Transactional(readOnly = true)
    public Employee requireAdmin(HttpServletRequest request) {
        Employee employee = authenticate(request);
        if (employee.getRole() != EmployeeRole.ADMIN) {
            throw new ForbiddenException("Only the office can do that.");
        }
        return employee;
    }

    /**
     * "First time here or forgot your password": emails a link if the address
     * belongs to an active employee. Says nothing either way to the caller.
     */
    @Transactional
    public void requestPasswordLink(String email) {
        Employee employee = employees.findByEmail(normalize(email)).orElse(null);
        if (employee == null || !employee.isActive()) return;

        Instant hourAgo = Instant.now().minus(Duration.ofHours(1));
        if (tokens.countByEmployeeIdAndCreatedAtAfter(employee.getId(), hourAgo) >= MAX_LINKS_PER_HOUR) {
            log.warn("Portal account {} asked for too many password links; ignoring", employee.getId());
            return;
        }
        sendPasswordLink(employee);
    }

    /** Emails an invite (no password yet) or a reset link (has one). */
    @Transactional
    public void sendPasswordLink(Employee employee) {
        boolean invite = employee.getPasswordHash() == null;
        Duration ttl = invite ? INVITE_TTL : RESET_TTL;

        String raw = Tokens.random();
        tokens.save(new EmployeeToken(employee.getId(), Tokens.sha256(raw), Instant.now().plus(ttl)));
        notifications.portalPasswordLink(employee, raw, invite, ttl.toHours());
    }

    @Transactional
    public void setPassword(String rawToken, String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new BadRequestException("Check the highlighted fields and try again.",
                    Map.of("password", "That password is too long. Use 72 characters or fewer."));
        }

        String expired = "This link has expired or was already used. Ask for a new one from the sign-in page.";
        EmployeeToken token = tokens.findByTokenHash(Tokens.sha256(rawToken))
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new BadRequestException(expired));
        Employee employee = employees.findById(token.getEmployeeId())
                .filter(Employee::isActive)
                .orElseThrow(() -> new BadRequestException(expired));

        employee.setPasswordHash(encoder.encode(password));
        employee.setFailedLogins(0);
        employee.setLockedUntil(null);

        // One use only, and a new password signs every device out.
        tokens.deleteByEmployeeId(employee.getId());
        sessions.deleteByEmployeeId(employee.getId());
        log.info("Portal password set for account {}", employee.getId());
    }

    /** Ends every session for an account, e.g. when the office deactivates it. */
    @Transactional
    public void signOutEverywhere(Long employeeId) {
        sessions.deleteByEmployeeId(employeeId);
        tokens.deleteByEmployeeId(employeeId);
    }

    public static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String bearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) return null;
        String token = header.substring(7).trim();
        return token.isEmpty() || token.length() > 200 ? null : token;
    }
}
