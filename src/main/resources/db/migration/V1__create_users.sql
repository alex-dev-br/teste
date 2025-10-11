CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tabela principal de usuários
CREATE TABLE IF NOT EXISTS users (
    id              UUID          PRIMARY KEY,
    name            VARCHAR(120)  NOT NULL,
    email           VARCHAR(180)  NOT NULL,
    login           VARCHAR(80)   NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    role            VARCHAR(20)   NOT NULL,      -- 'OWNER' | 'CLIENT'
    address_summary VARCHAR(255)  NULL,

    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- unicidade
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
