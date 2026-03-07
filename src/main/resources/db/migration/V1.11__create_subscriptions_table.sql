CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL,
    user_id UUID NOT NULL,
    count INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_subscription_session
        FOREIGN KEY (session_id)
        REFERENCES concours_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_subscription_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_session ON subscriptions(session_id);
