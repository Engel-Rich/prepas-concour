CREATE TABLE Cours (
    id         UUID         PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    body       TEXT,
    video_url  TEXT,
    matiere_id UUID         NOT NULL REFERENCES Matieres(id) ON DELETE CASCADE,
    user_id    UUID         REFERENCES Users(id) ON DELETE SET NULL,
    is_active  BOOLEAN      DEFAULT TRUE,
    metadata   JSONB,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
