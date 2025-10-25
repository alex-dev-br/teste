package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper.UserMapper;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import jakarta.inject.Named;

import java.util.Optional;

@Named
public record UserPersistenceAdapter(UserRepositoryJPA userRepositoryJPA) implements UserPersistence {

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
}
