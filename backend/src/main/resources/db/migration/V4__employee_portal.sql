-- Employee portal: accounts, licenses, one-time password links, and sign-in sessions.

CREATE TABLE employee (
    id                     BIGSERIAL PRIMARY KEY,
    email                  VARCHAR(180) NOT NULL UNIQUE,
    -- NULL until the employee follows their invite link and sets a password.
    password_hash          VARCHAR(100),
    access_role            VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE',
    active                 BOOLEAN      NOT NULL DEFAULT TRUE,

    first_name             VARCHAR(80)  NOT NULL,
    last_name              VARCHAR(80)  NOT NULL,
    phone                  VARCHAR(40),
    address_line1          VARCHAR(160),
    address_line2          VARCHAR(160),
    city                   VARCHAR(80),
    state                  VARCHAR(40),
    postal_code            VARCHAR(20),

    job_title              VARCHAR(120),
    department             VARCHAR(80),
    hire_date              DATE,
    supervisor             VARCHAR(120),

    emergency_name         VARCHAR(120),
    emergency_relationship VARCHAR(80),
    emergency_phone        VARCHAR(40),

    failed_logins          INTEGER      NOT NULL DEFAULT 0,
    locked_until           TIMESTAMPTZ,
    last_login_at          TIMESTAMPTZ,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE employee_license (
    id             BIGSERIAL PRIMARY KEY,
    employee_id    BIGINT       NOT NULL REFERENCES employee (id) ON DELETE CASCADE,
    name           VARCHAR(160) NOT NULL,
    license_number VARCHAR(80),
    issuer         VARCHAR(120),
    expires_on     DATE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_employee_license_employee ON employee_license (employee_id);

-- Invite and password-reset links. Only a SHA-256 hash of the token is stored.
CREATE TABLE employee_token (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT      NOT NULL REFERENCES employee (id) ON DELETE CASCADE,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_employee_token_employee ON employee_token (employee_id);

-- Sign-in sessions. Only a SHA-256 hash of the token is stored.
CREATE TABLE employee_session (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT      NOT NULL REFERENCES employee (id) ON DELETE CASCADE,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_employee_session_employee ON employee_session (employee_id);
