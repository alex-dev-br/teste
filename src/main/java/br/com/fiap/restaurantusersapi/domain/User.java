package br.com.fiap.restaurantusersapi.domain;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "ux_users_email", columnList = "email", unique = true),
                @Index(name = "ux_users_login", columnList = "login", unique = true)
        })
public class User implements UserDetails {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 80)
    private String login;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_hash",nullable = false, length = 255)
    private String passwordHash;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name="user_roles",
            joinColumns=@JoinColumn(name="user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"})   // Evitar duplicata no DB
    )
    @Column(name="role")
    private Set<Role> roles;


    //  Ciclo de vida:
    @PrePersist
    public void prePersist() {
        if (this.id == null) { this.id = UUID.randomUUID(); }
        this.email = email == null ? null : email.toLowerCase();
        this.login = login == null ? null : login.toLowerCase();
        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.email = email == null ? null : email.toLowerCase();
        this.login = login == null ? null : login.toLowerCase();
        this.updatedAt = Instant.now();
    }


    //  Getters and Setters:
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

    public Set<Role> getRoles() { return roles;}

    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Instant getCreatedAt() { return createdAt; }

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


    //  User Details:
    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) { return Set.of(); }
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore   // Evita expor a senha na serialização JSON
    public String getPassword() { return this.passwordHash; }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}