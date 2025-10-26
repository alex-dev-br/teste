package br.com.fiap.restaurantusersapi.application.ports.inbound.list;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ListUserOutput(UUID uuid, String name, String email, String login, ListAddressOutput address, Set<ListRoleOutput> roles, Instant createdAt, Instant updatedAt) {
    public ListUserOutput(User user) {
        this(user.uuid(), user.name(), user.email().address(), user.login(),
                user.address() != null ? new ListAddressOutput(user.address()) : null,
                user.roles().stream().map(ListRoleOutput::new).collect(Collectors.toSet()),
                user.createdAt(), user.updatedAt());
    }
}
