ALTER TABLE users
    ADD COLUMN IF NOT EXISTS pwd_must_change BOOLEAN      NOT NULL   DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS pwd_changed_at  TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS pwd_version     INTEGER      NOT NULL   DEFAULT 0;

-- DEFAULT -> aplica para as linhas antigas
