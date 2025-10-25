package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.user.Role;

public record CreateRoleOutput(String name) {
    public CreateRoleOutput(Role role) {
        this(role.name());
    }
}
