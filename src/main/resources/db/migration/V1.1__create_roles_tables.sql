CREATE TABLE IF NOT EXISTS Roles(
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

INSERT INTO roles (id, name) VALUES
(gen_random_uuid(), 'USER'),
(gen_random_uuid(), 'ADMIN'),
(gen_random_uuid(), 'TEACHER'),
(gen_random_uuid(), 'PARTNER'),
(gen_random_uuid(), 'COMMERCIAL')
ON CONFLICT (name) DO NOTHING;
