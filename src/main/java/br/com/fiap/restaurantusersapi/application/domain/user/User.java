package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record User(UUID uuid, String name, Email email, String login, Password password, Address address, Set<Role> roles, Instant createdAt, Instant updatedAt) {

    public User(UUID uuid, String name, Email email, String login, Password password, Address address, Set<Role> roles) {
        this(uuid, name, email, login, password, address, roles, null, null);
    }

    public User(String name, Email email, String login, Password password, Address address, Set<Role> roles) {
        this(null, name, email, login, password, address, roles, null, null);
    }

    public User {
        if (name == null || name.isBlank()) {
            throw new DomainException("Name cannot be null or empty");
        }
        if (email == null) {
            throw new DomainException("Email cannot be null");
        }
        if (login == null || login.isBlank()) {
            throw new DomainException("Login cannot be null or empty");
        }

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        if (createdAt == null && updatedAt == null) {
            Instant now = Instant.now();
            createdAt = now;
            updatedAt = now;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        login = login.toLowerCase();
    }
}
