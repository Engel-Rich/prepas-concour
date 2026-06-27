CREATE TABLE payment_services (
    id            UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255)     NOT NULL,
    description   TEXT,
    logo_url      VARCHAR(500),
    reg_exp       VARCHAR(500),
    rate          DOUBLE PRECISION,
    provider_rate DOUBLE PRECISION,
    provider_id   UUID             NOT NULL,
    is_active     BOOLEAN          NOT NULL DEFAULT TRUE,
    sens          VARCHAR(10)      NOT NULL,
    metadata      JSONB,
    created_at    TIMESTAMP        NOT NULL,
    updated_at    TIMESTAMP        NOT NULL,
    CONSTRAINT fk_payment_service_provider
        FOREIGN KEY (provider_id) REFERENCES payment_providers(id) ON DELETE CASCADE
);

CREATE INDEX idx_payment_services_provider ON payment_services(provider_id);
