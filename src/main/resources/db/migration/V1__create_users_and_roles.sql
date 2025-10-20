-- Extensão para suporte a UUIDs e funções criptográficas (bcrypt)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tabela principal de usuários (sem coluna 'role', papéis em user_roles)
CREATE TABLE IF NOT EXISTS users (
    id              UUID          PRIMARY KEY,
    name            VARCHAR(120)  NOT NULL,
    email           VARCHAR(180)  NOT NULL,
    login           VARCHAR(80)   NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
    );

-- unicidade (case insensitive) para email e login
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users (LOWER(email));
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_login ON users (LOWER(login));

-- trigger simples para updated_at
CREATE OR REPLACE FUNCTION trg_users_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS users_set_updated_at ON users;
CREATE TRIGGER users_set_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION trg_users_set_updated_at();

-- Tabela de papéis/roles dos usuários
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id   UUID          NOT NULL,
                                          role      VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Checagem de domínio para os papéis
ALTER TABLE user_roles
    ADD CONSTRAINT chk_user_roles_role CHECK (role IN ('CLIENT', 'OWNER', 'ADMIN'));

-- Índice para consultas por papel
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role);