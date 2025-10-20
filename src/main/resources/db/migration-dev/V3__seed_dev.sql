-- Usuário 1 (CLIENT)
INSERT INTO users (id, name, email, login, password_hash)
VALUES (
           gen_random_uuid(),
           'Maria Silva',
           'maria.silva@mail.com',
           'mariasilva',
           crypt('SenhaDev#123', gen_salt('bf', 12))
       );

INSERT INTO user_roles (user_id, role)
SELECT id, 'CLIENT' FROM users WHERE email = 'maria.silva@mail.com';

INSERT INTO addresses (user_id, street, number, complement, neighborhood, city, state, zip_code)
SELECT id, 'Rua das Flores', '100', NULL, 'Centro', 'São Paulo', 'SP', '01234-567'
FROM users WHERE email = 'maria.silva@mail.com';


-- Usuário 2 (OWNER)
INSERT INTO users (id, name, email, login, password_hash)
VALUES (
           gen_random_uuid(),
           'Carlos Souza',
           'carlos.souza@mail.com',
           'carlossouza',
           crypt('SenhaDev#123', gen_salt('bf', 12))
       );

INSERT INTO user_roles (user_id, role)
SELECT id, 'OWNER' FROM users WHERE email = 'carlos.souza@mail.com';

INSERT INTO addresses (user_id, street, number, complement, neighborhood, city, state, zip_code)
SELECT id, 'Av. Atlântica', '2000', 'Ap 12', 'Copacabana', 'Rio de Janeiro', 'RJ', '22000-000'
FROM users WHERE email = 'carlos.souza@mail.com';