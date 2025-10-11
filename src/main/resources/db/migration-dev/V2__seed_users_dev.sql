INSERT INTO users (id, name, email, login, password_hash, role, address_summary)
VALUES
    (gen_random_uuid(),
     'Maria Silva',
     'maria.silva@mail.com',
     'mariasilva',
     '{bcrypt}$2a$10$dummyhash',
     'CLIENT',
     'Rio de Janeiro, RJ'
    )
    ON CONFLICT DO NOTHING;
