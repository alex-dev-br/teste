package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public record User(UUID uuid, String name, Email email, String login, String password, Address address, Set<Role> roles, Instant createdAt, Instant updatedAt) {

    private static final String PASSWORD_ERROR_MESSAGE = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&).";
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public User(String name, Email email, String login, String password, Address address, Set<Role> roles) {
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
        if (password == null || !STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new DomainException(PASSWORD_ERROR_MESSAGE);
        }
        if (roles == null || roles.isEmpty()) {
            throw new DomainException("Roles cannot be null or empty");
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
