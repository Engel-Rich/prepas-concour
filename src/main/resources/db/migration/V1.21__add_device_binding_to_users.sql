-- Liaison compte <-> appareil actif (un seul appareil par compte).
-- Colonnes nullables : les comptes créés avant cette fonctionnalité n'ont pas
-- encore d'appareil connu et seront liés lors de leur prochaine requête
-- authentifiée ou de leur prochaine connexion.

ALTER TABLE Users
    ADD COLUMN IF NOT EXISTS device_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS platform  VARCHAR(16);

-- Recherche par appareil (diagnostic, détection de partage de compte)
CREATE INDEX IF NOT EXISTS idx_users_device_id ON Users(device_id);