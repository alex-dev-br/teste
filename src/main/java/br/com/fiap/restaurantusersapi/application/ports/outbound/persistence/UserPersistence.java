package br.com.fiap.restaurantusersapi.application.ports.outbound.persistence;

import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;
import br.com.fiap.restaurantusersapi.application.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserPersistence {
    User create(User user);
    Optional<User> findByEmail(String address);
    Optional<User> findByLogin(String login);
    Optional<User> findByUuid(UUID uuid);
    Pagination<User> findByName(String name, Page page);
    void deleteByUuid(UUID uuid);
    void changePassword(UUID uuid, String newPassword);
}
