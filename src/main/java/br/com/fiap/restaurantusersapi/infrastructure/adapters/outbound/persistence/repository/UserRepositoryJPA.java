package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryJPA extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByLoginIgnoreCase(String login);

    List<UserEntity> findAllByNameIgnoreCase(String name);

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.name) = LOWER(:name)")
    Page<UserEntity> findAllByName(@Param("name") String name, Pageable pageable);

    Optional<UserEntity> findByLogin(@NotBlank String login);
}
