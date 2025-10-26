package br.com.fiap.restaurantusersapi.application.ports.inbound.list;

import br.com.fiap.restaurantusersapi.application.domain.user.Role;

public record ListRoleOutput(String name) {
    public ListRoleOutput(Role role) {
        this(role.name());
    }
}
