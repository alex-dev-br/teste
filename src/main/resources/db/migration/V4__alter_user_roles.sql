-- Remove a restrição de verificação de papéis existente
ALTER TABLE user_roles DROP CONSTRAINT chk_user_roles_role;

-- Atualiza os papéis de 'CLIENT' para 'CUSTOMER'
UPDATE user_roles SET role = 'CUSTOMER' WHERE role = 'CLIENT';

-- Adiciona a nova restrição de verificação de papéis com 'CUSTOMER'
ALTER TABLE user_roles ADD CONSTRAINT chk_user_roles_role CHECK (role IN ('CUSTOMER', 'OWNER', 'ADMIN'));
