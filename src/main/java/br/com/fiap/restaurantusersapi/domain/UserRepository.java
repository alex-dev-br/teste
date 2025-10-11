package br.com.fiap.restaurantusersapi.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByLoginIgnoreCase(String login);
    Optional<User> findByNameIgnoreCase(String name);
}
