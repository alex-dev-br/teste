package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateUserOutput(UUID uuid, String name, String email, String login, CreateAddressOutput address, Set<CreateRoleOutput> roles, Instant createdAt, Instant updatedAt) {
    public CreateUserOutput(User user) {
        this(user.uuid(), user.name(), user.email().address(), user.login(),
                user.address() != null ? new CreateAddressOutput(user.address()) : null,
                user.roles().stream().map(CreateRoleOutput::new).collect(Collectors.toSet()),
                user.createdAt(), user.updatedAt());
    }
}
