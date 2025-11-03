package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper;

import br.com.fiap.restaurantusersapi.application.domain.user.*;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {}

    public static UserEntity toEntity(User user) {
        var userEntity = new UserEntity();
        userEntity.setUuid(user.uuid());
        userEntity.setName(user.name());

        userEntity.setEmail(user.email().address());

        userEntity.setLogin(user.login());
        userEntity.setPasswordHash(user.password().value());

        // Domínio -> Enum JPA
        userEntity.setRoles(
                user.roles() == null ? Set.of() :
                        user.roles().stream()
                                .map(r -> RoleEntity.valueOf(r.name())) // r.name() é a String do VO Role (ex.: "ADMIN")
                                .collect(Collectors.toSet())
        );

        userEntity.setCreatedAt(user.createdAt());
        userEntity.setUpdatedAt(user.updatedAt());

        if (user.address() != null) {
            var addressEntity = AddressMapper.toEntity(user.address(), userEntity);
            userEntity.setAddress(addressEntity);
        }

        return userEntity;
    }

    public static User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getUuid(),
                userEntity.getName(),
                new Email(userEntity.getEmail()),
                userEntity.getLogin(),
                Password.hashed(userEntity.getPasswordHash()),
                userEntity.getAddress() != null ? AddressMapper.toDomain(userEntity.getAddress()) : null,
                (userEntity.getRoles() == null ? Set.<Role>of() :
                        userEntity.getRoles().stream()
                                .map(r -> new Role(r.name())) // normaliza no VO (UPPERCASE) se você aplicou a dica
                                .collect(Collectors.toSet())
                ),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }
}
