CREATE TYPE session_status AS ENUM ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELED');

CREATE TABLE Concours_Sessions (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    concours_id UUID REFERENCES Concours(id) ON DELETE CASCADE,
    status  session_status DEFAULT 'UPCOMING',
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);