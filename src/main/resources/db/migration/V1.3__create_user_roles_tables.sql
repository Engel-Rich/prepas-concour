CREATE TABLE users_roles (
    users_id UUID REFERENCES Users(id) ON DELETE CASCADE,
    roles_id UUID REFERENCES Roles(id) ON DELETE CASCADE,
    PRIMARY KEY (users_id, roles_id)
);
