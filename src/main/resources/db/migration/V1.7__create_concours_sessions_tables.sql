CREATE TYPE session_status AS ENUM ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELED');

CREATE TABLE Concours_Sessions (
    id          UUID           PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    concours_id UUID           REFERENCES Concours(id) ON DELETE CASCADE,
    status      session_status DEFAULT 'UPCOMING',
    start_date  DATE           NOT NULL,
    end_date    DATE           NOT NULL,
    is_active   BOOLEAN        DEFAULT TRUE,
    metadata    JSONB,
    created_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);
