package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper;

import br.com.fiap.restaurantusersapi.application.domain.user.*;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;

import java.util.stream.Collectors;

public record UserMapper() {
    public static UserEntity toEntity(User user) {
        var userEntity = new UserEntity();
        userEntity.setUuid(user.uuid());
        userEntity.setName(user.name());
        userEntity.setEmail(user.email().address());
        userEntity.setLogin(user.login());
        userEntity.setPasswordHash(user.password().value());
        userEntity.setRoles(user.roles().stream().map(r -> RoleEntity.valueOf(r.name())).collect(Collectors.toSet()));
        userEntity.setCreatedAt(user.createdAt());
        userEntity.setUpdatedAt(user.updatedAt());

        if (user.address() != null) {
            var addressEntity = AddressMapper.toEntity(user.address(), userEntity);
            userEntity.setAddress(addressEntity);
        }

        return userEntity;
    }

    public static User toDomain(UserEntity userEntity) {
        return new User (
            userEntity.getUuid(),
                userEntity.getName(),
                new Email(userEntity.getEmail()),
                userEntity.getLogin(),
                new Password(userEntity.getPassword(), true),
                userEntity.getAddress() != null ? AddressMapper.toDomain(userEntity.getAddress()) : null,
                userEntity.getRoles().stream().map(r -> new Role(r.name())).collect(Collectors.toSet()),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }
}
