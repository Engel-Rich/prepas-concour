-- Association entre sessions de concours et matières
CREATE TABLE IF NOT EXISTS concours_session_matieres (
    id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID      NOT NULL REFERENCES concours_sessions(id) ON DELETE CASCADE,
    matiere_id UUID      NOT NULL REFERENCES Matieres(id)          ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (session_id, matiere_id)
);

-- Association entre sessions de concours et cours (chapitres)
CREATE TABLE IF NOT EXISTS concours_session_cours (
    id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID      NOT NULL REFERENCES concours_sessions(id) ON DELETE CASCADE,
    cours_id   UUID      NOT NULL REFERENCES Cours(id)             ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (session_id, cours_id)
);

CREATE INDEX IF NOT EXISTS idx_csm_session ON concours_session_matieres(session_id);
CREATE INDEX IF NOT EXISTS idx_csm_matiere ON concours_session_matieres(matiere_id);
CREATE INDEX IF NOT EXISTS idx_csc_session ON concours_session_cours(session_id);
CREATE INDEX IF NOT EXISTS idx_csc_cours   ON concours_session_cours(cours_id);
