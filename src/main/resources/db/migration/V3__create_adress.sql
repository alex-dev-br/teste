-- Criação da tabela de endereços
CREATE TABLE addresses (
    address_id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    street VARCHAR(255),
    number VARCHAR(20),
    complement VARCHAR(100),
    neighborhood VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(2),
    zip_code VARCHAR(9)
);

-- Adiciona a chave estrangeira para a tabela de usuários
-- O endereço é deletado se o usuário for deletado (ON DELETE CASCADE)
ALTER TABLE addresses
ADD CONSTRAINT fk_address_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Adiciona uma constraint de unicidade para garantir que um usuário tenha apenas um endereço
ALTER TABLE addresses ADD CONSTRAINT uk_address_user_id UNIQUE (user_id);

-- Cria um índice no campo user_id para otimizar as buscas
CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);

-- V2: Insere um registro de endereço padrão para cada usuário existente
-- que ainda não possui um endereço associado.

INSERT INTO addresses (user_id, street, number, complement, neighborhood, city, state, zip_code)
SELECT
    u.id, '', '', '', '', '', '', ''
FROM users u
LEFT JOIN addresses a ON u.id = a.user_id
WHERE a.address_id IS NULL;

-- V4: Remove a coluna address_summary da tabela users, pois ela tem uma tabela propria agora
ALTER TABLE users DROP COLUMN IF EXISTS address_summary;