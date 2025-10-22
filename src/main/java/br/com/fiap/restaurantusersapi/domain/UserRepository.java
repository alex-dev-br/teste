package br.com.fiap.restaurantusersapi.domain;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByLoginIgnoreCase(String login);
    List<User> findAllByNameIgnoreCase(String name);
    Optional<User> findByLogin(@NotBlank String login);
}
