package br.com.fiap.restaurantusersapi.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "users",
        indexes = {
            @Index(name = "ux_users_email", columnList = "email", unique = true),
            @Index(name = "ux_users_login", columnList = "login", unique = true)
        })
public class User {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 80)
    private String login;

    @Column(name = "password_hash",nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role = "CLIENT";   // "OWNER" ou "CLIENT"   (Colocar Enum?)

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
