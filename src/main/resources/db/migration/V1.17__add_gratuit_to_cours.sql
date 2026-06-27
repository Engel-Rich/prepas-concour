-- Indique si un cours est gratuit ou payant (défaut : payant)
ALTER TABLE Cours ADD COLUMN IF NOT EXISTS gratuit BOOLEAN NOT NULL DEFAULT FALSE;
