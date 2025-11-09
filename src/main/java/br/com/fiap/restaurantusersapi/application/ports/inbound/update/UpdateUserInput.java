package br.com.fiap.restaurantusersapi.application.ports.inbound.update;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.user.Email;
import br.com.fiap.restaurantusersapi.application.domain.user.Password;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.ToDomainMapper;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateAddressInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateRoleInput;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UpdateUserInput(UUID userUuid, String name, String email, String login, UpdateAddressInput address, Set<UpdateRoleInput> roles) implements ToDomainMapper<User> {

    @Override
    public User toDomain() throws DomainException {
        return new User (
            this.userUuid, this.name, new Email(this.email), this.login, new Password("secret", true),
                this.address != null ? this.address.toDomain() : null,
                this.roles == null ? null : this.roles.stream().map(UpdateRoleInput::toDomain).collect(Collectors.toSet())
        );
    }
}
