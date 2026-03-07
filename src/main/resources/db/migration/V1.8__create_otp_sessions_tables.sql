CREATE TYPE otp_session_type AS ENUM ('REGISTRATION', 'LOGIN', 'PASSWORD_RESET', 'EMAIL_VERIFICATION', 'PHONE_VERIFICATION');

CREATE TABLE IF NOT EXISTS OtpSessions (
    id UUID PRIMARY KEY,
    email VARCHAR(120) ,
    phone VARCHAR(20) ,
    otp_type otp_session_type NOT NULL DEFAULT 'LOGIN',
    full_name TEXT,
    otp_hash TEXT,
    metadata JSONB,
    expires_at BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);