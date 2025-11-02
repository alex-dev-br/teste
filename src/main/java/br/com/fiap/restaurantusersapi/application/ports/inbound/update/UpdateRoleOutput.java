package br.com.fiap.restaurantusersapi.application.ports.inbound.update;

import br.com.fiap.restaurantusersapi.application.domain.user.Role;

public record UpdateRoleOutput(String name) {
    public UpdateRoleOutput(Role role) {
        this(role.name());
    }
}
