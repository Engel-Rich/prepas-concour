
CREATE TABLE payment_services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    logo_url VARCHAR(500),
    regexp VARCHAR(500),
    rate DOUBLE PRECISION,
    provider_id UUID NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sens VARCHAR(10) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_service_provider
        FOREIGN KEY (provider_id)
        REFERENCES payment_providers(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_payment_services_provider ON payment_services(provider_id);
