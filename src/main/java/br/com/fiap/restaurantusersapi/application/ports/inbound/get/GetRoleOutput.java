package br.com.fiap.restaurantusersapi.application.ports.inbound.get;

import br.com.fiap.restaurantusersapi.application.domain.user.Role;

public record GetRoleOutput(String name) {
    public GetRoleOutput(Role role) {
        this(role.name());
    }
}
