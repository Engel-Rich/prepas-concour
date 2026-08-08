-- Traçabilité de la consommation d'un code d'activation.
-- Un code n'est utilisable qu'une seule fois : on enregistre qui l'a activé,
-- quand, et la souscription créée à son profit.

ALTER TABLE subscription_codes
    ADD COLUMN IF NOT EXISTS used_by_user_id           UUID,
    ADD COLUMN IF NOT EXISTS used_at                   TIMESTAMP,
    ADD COLUMN IF NOT EXISTS activated_subscription_id UUID;

-- L'activateur et la souscription générée sont conservés même si la ligne
-- référencée disparaît : la trace d'audit prime (ON DELETE SET NULL).
ALTER TABLE subscription_codes
    ADD CONSTRAINT fk_subscription_code_used_by
        FOREIGN KEY (used_by_user_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE subscription_codes
    ADD CONSTRAINT fk_subscription_code_activated_subscription
        FOREIGN KEY (activated_subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_subscription_codes_used_by ON subscription_codes(used_by_user_id);
CREATE INDEX IF NOT EXISTS idx_subscription_codes_status  ON subscription_codes(status);
CREATE INDEX IF NOT EXISTS idx_subscription_codes_code    ON subscription_codes(code);
