package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence;

import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper.UserMapper;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import jakarta.inject.Named;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userRepositoryJPA.findById(uuid).map(UserMapper::toDomain);
    }

    @Override
    public Pagination<User> findByName(String name, Page page) {
        var pageRequest = PageRequest.of(page.pageNumber(), page.pageSize(), Sort.by(Sort.Direction.ASC, "name"));
        var pageResult = userRepositoryJPA.findAllByName(name, pageRequest);
        return new Pagination<> (
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
}
