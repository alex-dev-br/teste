package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryJPA extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    Optional<UserEntity> findByLoginIgnoreCase(String login);
    List<UserEntity> findAllByNameIgnoreCase(String name);
    Optional<UserEntity> findByLogin(@NotBlank String login);
}
