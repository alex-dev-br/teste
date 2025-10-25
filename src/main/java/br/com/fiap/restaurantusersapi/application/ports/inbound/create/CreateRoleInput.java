package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.user.Role;
import br.com.fiap.restaurantusersapi.application.ports.ToDomainMapper;

public record CreateRoleInput(String name) implements ToDomainMapper<Role> {

    @Override
    public Role toDomain() {
        return new Role(this.name);
    }
}
