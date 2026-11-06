-- Core tables for the Capital City Mechanical website.

CREATE TABLE service_offering (
    id            BIGSERIAL PRIMARY KEY,
    slug          VARCHAR(80)  NOT NULL UNIQUE,
    name          VARCHAR(120) NOT NULL,
    category      VARCHAR(20)  NOT NULL,
    summary       VARCHAR(500) NOT NULL,
    display_order INTEGER      NOT NULL DEFAULT 0,
    active        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE service_offering_point (
    service_offering_id BIGINT       NOT NULL REFERENCES service_offering (id) ON DELETE CASCADE,
    position            INTEGER      NOT NULL,
    point               VARCHAR(240) NOT NULL,
    PRIMARY KEY (service_offering_id, position)
);

CREATE TABLE contact_request (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(120)  NOT NULL,
    email         VARCHAR(180)  NOT NULL,
    phone         VARCHAR(40)   NOT NULL,
    property_type VARCHAR(20)   NOT NULL,
    service_type  VARCHAR(80)   NOT NULL,
    urgency       VARCHAR(20)   NOT NULL,
    message       VARCHAR(4000) NOT NULL,
    status        VARCHAR(20)   NOT NULL DEFAULT 'NEW',
    source_ip     VARCHAR(64),
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contact_request_created_at ON contact_request (created_at DESC);
CREATE INDEX idx_contact_request_status ON contact_request (status);

CREATE TABLE job_posting (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(120)  NOT NULL,
    department      VARCHAR(80)   NOT NULL,
    location        VARCHAR(120)  NOT NULL,
    employment_type VARCHAR(60)   NOT NULL,
    description     VARCHAR(2000) NOT NULL,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    posted_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_job_posting_active ON job_posting (active);

CREATE TABLE job_posting_requirement (
    job_posting_id BIGINT       NOT NULL REFERENCES job_posting (id) ON DELETE CASCADE,
    position       INTEGER      NOT NULL,
    requirement    VARCHAR(240) NOT NULL,
    PRIMARY KEY (job_posting_id, position)
);

CREATE TABLE job_application (
    id               BIGSERIAL PRIMARY KEY,
    job_posting_id   BIGINT        NOT NULL REFERENCES job_posting (id),
    first_name       VARCHAR(80)   NOT NULL,
    last_name        VARCHAR(80)   NOT NULL,
    email            VARCHAR(180)  NOT NULL,
    phone            VARCHAR(40)   NOT NULL,
    years_experience INTEGER       NOT NULL,
    licenses         VARCHAR(300),
    resume_url       VARCHAR(500),
    cover_note       VARCHAR(4000),
    status           VARCHAR(20)   NOT NULL DEFAULT 'RECEIVED',
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_job_application_posting ON job_application (job_posting_id);
CREATE INDEX idx_job_application_created_at ON job_application (created_at DESC);
