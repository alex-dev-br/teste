package br.com.fiap.restaurantusersapi.application.ports.outbound.persistence;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.util.Optional;

public interface UserPersistence {
    User create(User user);
    Optional<User> findByEmail(String address);
    Optional<User> findByLogin(String login);
}
