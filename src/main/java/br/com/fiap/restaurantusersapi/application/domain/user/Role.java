package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

import java.util.Locale;

public record Role(String name) {

    public static final Role ADMIN    = new Role("ADMIN");
    public static final Role OWNER    = new Role("OWNER");
    public static final Role CUSTOMER = new Role("CUSTOMER");

    public static Role of(String name) { return new Role(name); }

    public Role {
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name cannot be null or empty");
        }
        // normaliza para manter compatibilidade com RoleEntity.valueOf(...)
        name = name.trim().toUpperCase(Locale.ROOT);
    }
}
