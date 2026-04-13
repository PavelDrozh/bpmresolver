INSERT INTO users (username, password, enabled)
VALUES ('admin', '{noop}admin', TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, enabled)
VALUES ('user', '{noop}user', TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_ADMIN'
FROM users u
WHERE u.username = 'admin'
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_USER'
FROM users u
WHERE u.username = 'admin'
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_BPM'
FROM users u
WHERE u.username = 'admin'
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_USER'
FROM users u
WHERE u.username = 'user'
ON CONFLICT (user_id, role) DO NOTHING;
