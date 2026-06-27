CREATE TABLE IF NOT EXISTS Concours (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    logo_url    TEXT,
    is_active   BOOLEAN      DEFAULT TRUE,
    metadata    JSONB,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
