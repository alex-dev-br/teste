-- Endereços 1–1 com users (lado dono é addresses.user_id)
CREATE TABLE IF NOT EXISTS addresses (
                                         address_id    BIGSERIAL      PRIMARY KEY,
                                         user_id       UUID           NOT NULL      UNIQUE,
                                         street        VARCHAR(255),
                                         number        VARCHAR(20),
                                         complement    VARCHAR(100),
                                         neighborhood  VARCHAR(100),
                                         city          VARCHAR(100),
                                         state         VARCHAR(2),
                                         zip_code      VARCHAR(9)
);



-- Foreign key para users com delete cascade
ALTER TABLE addresses
    ADD CONSTRAINT fk_address_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- (opcional) sanity checks => para evitar valores inválidos.
ALTER TABLE addresses
    ADD CONSTRAINT ck_addresses_state
        CHECK (state IS NULL OR state ~ '^[A-Z]{2}$');

ALTER TABLE addresses
    ADD CONSTRAINT ck_addresses_zip
        CHECK (zip_code IS NULL OR zip_code ~ '^[0-9]{5}-?[0-9]{3}$');


