package br.com.fiap.restaurantusersapi.application.ports.inbound.get;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record GetUserOutput(UUID uuid, String name, String email, String login, GetAddressOutput address, Set<GetRoleOutput> roles, Instant createdAt, Instant updatedAt) {
    public GetUserOutput(User user) {
        this(user.uuid(), user.name(), user.email().address(), user.login(),
                user.address() != null ? new GetAddressOutput(user.address()) : null,
                user.roles().stream().map(GetRoleOutput::new).collect(Collectors.toSet()),
                user.createdAt(), user.updatedAt());
    }
}
