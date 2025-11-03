package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;
import br.com.fiap.restaurantusersapi.application.domain.user.Address;
import br.com.fiap.restaurantusersapi.application.domain.user.Role;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.CountAdminsPort;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.SaveUserWithPolicyPort;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.AddressEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper.AddressMapper;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper.UserMapper;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import jakarta.inject.Named;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
public record UserPersistenceAdapter(UserRepositoryJPA userRepositoryJPA)
        implements UserPersistence, CountAdminsPort, SaveUserWithPolicyPort {

    @Override
    public long countAdmins() {
        return userRepositoryJPA.countAdmins();
    }

    @Override
    public User saveAdminWithPolicy(User userDomain, boolean forceChangeOnFirstLogin) {
        // Mapeia domínio -> entidade
        UserEntity mapped = UserMapper.toEntity(userDomain);

        // Garante o ID vindo do domínio (merge se existir, persist se novo)
        mapped.setUuid(userDomain.uuid());

        // Política inicial de credenciais
        mapped.setPwdMustChange(forceChangeOnFirstLogin);
        mapped.setPwdVersion(0);

        // Salva e volta para domínio
        UserEntity saved = userRepositoryJPA.save(mapped);
        return UserMapper.toDomain(saved);
    }

    @Override
    public User create(User user) {
        UserEntity createdUser = userRepositoryJPA.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(createdUser);
    }

    @Override
    public Optional<User> findByEmail(String address) {
        return userRepositoryJPA.findByEmailIgnoreCase(address).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepositoryJPA.findByLoginIgnoreCase(login).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userRepositoryJPA.findById(uuid).map(UserMapper::toDomain);
    }

    @Override
    public Pagination<User> findByName(String name, Page page) {
        var pageRequest = PageRequest.of(page.pageNumber(), page.pageSize(),
                Sort.by(Sort.Direction.ASC, "name"));
        var pageResult = userRepositoryJPA.findAllByName(name, pageRequest);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getContent().stream().map(UserMapper::toDomain).toList()
        );
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        userRepositoryJPA.deleteById(uuid);
    }

    @Override
    public void changePassword(UUID uuid, String newPassword) {
        userRepositoryJPA.changePassword(uuid, newPassword);
    }

    @Override
    public User update(User user) {
        // Carrega a entidade existente
        UserEntity entity = userRepositoryJPA.findById(user.uuid())
                .orElseThrow(() -> new DomainException("Usuário alvo das alterações não foi encontrado"));

        // ---- Campos simples (mantém createdAt, passwordHash e flags de senha) ----
        entity.setName(user.name());
        entity.setEmail(user.email().address());
        entity.setLogin(user.login());
        entity.setUpdatedAt(Instant.now());

        // ---- Roles: mapeia VO -> Enum JPA ----
        Set<RoleEntity> newRoles = (user.roles() == null ? Set.<RoleEntity>of() :
                user.roles().stream()
                        .map(Role::name)            // "ADMIN", "OWNER", "CUSTOMER"
                        .map(RoleEntity::valueOf)   // Enum do JPA
                        .collect(Collectors.toSet())
        );
        entity.setRoles(newRoles);

        // ---- Endereço: atualiza se veio no payload; se não vier, mantém o atual ----
        Address newAddress = user.address();
        AddressEntity current = entity.getAddress();

        if (newAddress != null) {
            if (current == null) {
                // não havia endereço: cria e associa
                AddressEntity created = AddressMapper.toEntity(newAddress, entity);
                entity.setAddress(created);
            } else {
                // havia endereço: atualiza campo a campo (preserva addressId)
                current.setStreet(newAddress.street());
                current.setNumber(newAddress.number());
                current.setComplement(newAddress.complement());
                current.setNeighborhood(newAddress.neighborhood());
                current.setCity(newAddress.city());
                current.setState(newAddress.state());
                current.setZipCode(newAddress.zipCode());
            }
        }
        // Se newAddress == null -> mantemos o endereço já existente (sem apagar)

        // Persiste e retorna domínio
        UserEntity saved = userRepositoryJPA.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<String> getUserPassword(UUID uuid) {
        return userRepositoryJPA.getUserPassword(uuid);
    }
}
