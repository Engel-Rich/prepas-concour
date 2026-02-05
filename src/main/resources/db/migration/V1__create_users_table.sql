CREATE TYPE user_role AS ENUM ('ADMIN', 'USER','TEACHER', 'PARTNER', 'COMMERCIAL');

CREATE TABLE IF NOT EXISTS Users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) ,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20)  UNIQUE,
    firebaseUid VARCHAR(255) UNIQUE NOT NULL,
    fcmToken VARCHAR(255) ,
    profilePictureUrl TEXT ,
    passwordHash VARCHAR(255) ,
    roles user_role[] NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    hasPhoneVerified BOOLEAN DEFAULT FALSE,
    hasEmailVerified BOOLEAN DEFAULT FALSE,
    lastLogin TIMESTAMP,
    metadata JSONB,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);