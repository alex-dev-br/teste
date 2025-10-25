package br.com.fiap.restaurantusersapi.application.ports.outbound.repository;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    User create(User user);
    Optional<User> findByEmail(String address);
}
