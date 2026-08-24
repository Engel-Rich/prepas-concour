-- Chiffrement des vidéos de cours.
--
-- has_been_crypted : distingue les vidéos stockées chiffrées de celles
--   déposées avant l'introduction du chiffrement. FALSE par défaut pour que
--   toutes les vidéos existantes restent lisibles telles quelles.
-- encryption_error : dernier motif d'échec, exposé à la console d'administration.

ALTER TABLE Cours
    ADD COLUMN IF NOT EXISTS has_been_crypted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS encryption_error TEXT;

-- Repérage rapide des vidéos restant à chiffrer
CREATE INDEX IF NOT EXISTS idx_cours_has_been_crypted ON Cours(has_been_crypted);

-- Clés de contenu, une par vidéo.
--
-- La clé n'est jamais stockée en clair : wrapped_key contient la clé de contenu
-- (CEK) chiffrée par la clé maîtresse du serveur (KEK), selon le schéma
-- d'enveloppe. Table séparée de Cours pour que le secret ne parte pas dans
-- chaque SELECT de cours.
CREATE TABLE IF NOT EXISTS cours_video_keys (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    cours_id     UUID         NOT NULL UNIQUE REFERENCES Cours(id) ON DELETE CASCADE,
    key_id       UUID         NOT NULL,
    wrapped_key  TEXT         NOT NULL,
    algorithm    VARCHAR(64)  NOT NULL DEFAULT 'AES-256-GCM/CHUNKED',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cours_video_keys_cours ON cours_video_keys(cours_id);
