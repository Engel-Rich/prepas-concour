CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference VARCHAR(255) NOT NULL UNIQUE,
    amount DOUBLE PRECISION NOT NULL,
    subscription_id UUID NOT NULL,
    user_id UUID NOT NULL,
    payment_service_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    sens VARCHAR(10) NOT NULL,
    pay_token VARCHAR(255),
    external_id VARCHAR(255),
    raison_reject TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transaction_subscription
        FOREIGN KEY (subscription_id)
        REFERENCES subscriptions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transaction_payment_service
        FOREIGN KEY (payment_service_id)
        REFERENCES payment_services(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_subscription ON transactions(subscription_id);
CREATE INDEX idx_transactions_payment_service ON transactions(payment_service_id);
