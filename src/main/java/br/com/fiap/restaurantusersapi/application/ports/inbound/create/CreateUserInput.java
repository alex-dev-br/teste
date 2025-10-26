package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.user.Email;
import br.com.fiap.restaurantusersapi.application.domain.user.Password;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.ToDomainMapper;

import java.util.Set;
import java.util.stream.Collectors;

public record CreateUserInput(String name, String email, String login, String password, CreateAddressInput address, Set<CreateRoleInput> roles) implements ToDomainMapper<User> {

    @Override
    public User toDomain() throws DomainException {
        return new User(
            this.name, new Email(this.email), this.login, new Password(this.password), this.address != null ? this.address.toDomain() : null, this.roles.stream().map(CreateRoleInput::toDomain).collect(Collectors.toSet())
        );
    }
}
