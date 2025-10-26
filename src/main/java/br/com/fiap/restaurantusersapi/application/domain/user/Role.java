package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

public record Role(String name) {
    public Role {
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name cannot be null or empty");
        }
    }
}
