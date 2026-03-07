--CREATE TYPE user_role AS ENUM ('ADMIN', 'USER','TEACHER', 'PARTNER', 'COMMERCIAL');

CREATE TABLE IF NOT EXISTS Users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) ,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20)  UNIQUE,
    firebase_uid VARCHAR(255) UNIQUE NOT NULL,
    fcm_token VARCHAR(255) ,
    profile_picture_url TEXT ,
    password_hash VARCHAR(255) ,
    is_active BOOLEAN DEFAULT TRUE,
    has_phone_verified BOOLEAN DEFAULT FALSE,
    has_email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);