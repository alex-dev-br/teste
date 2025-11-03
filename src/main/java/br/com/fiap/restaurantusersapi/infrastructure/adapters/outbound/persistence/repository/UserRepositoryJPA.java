package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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

    Optional<UserEntity> findByLogin(String login);

    @Transactional
    @Modifying
    @Query("""
           UPDATE UserEntity u
              SET u.passwordHash  =  :newPassword,
                  u.updatedAt     =  CURRENT_TIMESTAMP,
                  u.pwdMustChange =  FALSE,
                  u.pwdChangedAt  =  CURRENT_TIMESTAMP,
                  u.pwdVersion    =  u.pwdVersion + 1
            WHERE u.uuid = :uuid
           """)
    void changePassword(@Param("uuid") UUID uuid, @Param("newPassword") String newPassword);

    @Query("SELECT u.passwordHash FROM UserEntity u WHERE u.uuid = :uuid")
    Optional<String> getUserPassword(@Param("uuid") UUID uuid);

    // >>> adição para o Bootstrap: conta quantos ADMIN existem
    @Query("""
           SELECT count(u)
             FROM UserEntity u
             JOIN u.roles r
            WHERE r = br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity.ADMIN
           """)
    long countAdmins();
}
