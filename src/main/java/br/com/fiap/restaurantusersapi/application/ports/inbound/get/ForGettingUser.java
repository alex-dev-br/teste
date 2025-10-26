package br.com.fiap.restaurantusersapi.application.ports.inbound.get;

import java.util.Optional;
import java.util.UUID;

public interface ForGettingUser {
    Optional<GetUserOutput> findByUuid(UUID uuid);
    Optional<GetUserOutput> findEmail(String email);
    Optional<GetUserOutput> findByLogin(String login);
}
