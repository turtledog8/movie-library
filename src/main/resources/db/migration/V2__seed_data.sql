INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('USER');

-- passwords are BCrypt hashes of the plaintext shown in each comment
INSERT INTO users (username, password, enabled) VALUES
    ('admin',         '$2a$10$hQG5X65eBB8qtwZjmrQkfOeggFKTTYDXqOYT.T8zsxDwH7GTQ..LW', TRUE), -- admin123
    ('user',          '$2a$10$9faLf2sX.j1GiUWgehuN9eUSxNxTay7FazlJvX/VSChv4YbHhboDi', TRUE), -- user123
    ('gergincho',     '$2a$10$bG3vybRl60dBwBuKGlLYReFP01266I.DE.euo6nIAno.UAfH4lNpO', TRUE), -- gergincho123
    ('parashkevica',  '$2a$10$dUerIhVgEF35dPo9PSv7VODdG0TJsKTWjv2AR5AJkuwlBnfusFgVW', TRUE); -- parashkevica123

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'user' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'gergincho' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'parashkevica' AND r.name IN ('ADMIN', 'USER');

INSERT INTO movies (title, director, release_year, rating) VALUES
    ('Obsession', 'Brian De Palma', 1976, 7.0),
    ('Raw', 'Julia Ducournau', 2016, 6.6),
    ('Byzantium', 'Neil Jordan', 2012, 6.3),
    ('The Emoji Movie', 'Tony Leondis', 2017, 5.6),
    ('The Room Returns!', 'Tommy Wiseau', 2024, NULL),
    ('Cunk on Life','Al Campbell', 2024, NULL);
