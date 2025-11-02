package br.com.fiap.restaurantusersapi.application.ports.inbound.update;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UpdateUserOutput(UUID uuid, String name, String email, String login, UpdateAddressOutput address, Set<UpdateRoleOutput> roles, Instant createdAt, Instant updatedAt) {
    public UpdateUserOutput(User user) {
        this(user.uuid(), user.name(), user.email().address(), user.login(),
                user.address() != null ? new UpdateAddressOutput(user.address()) : null,
                user.roles().stream().map(UpdateRoleOutput::new).collect(Collectors.toSet()),
                user.createdAt(), user.updatedAt());
    }
}
