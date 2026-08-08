ALTER TABLE concours_session_matieres
    ADD COLUMN IF NOT EXISTS duree_minutes INTEGER,
    ADD COLUMN IF NOT EXISTS coefficient   DOUBLE PRECISION;
