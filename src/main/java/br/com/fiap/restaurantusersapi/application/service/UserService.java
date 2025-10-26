package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.ForCreatingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.ForGettingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.GetUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;
import br.com.fiap.restaurantusersapi.application.service.validator.CreateUserValidator;
import jakarta.inject.Named;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Named
public class UserService implements ForCreatingUser, ForGettingUser {

    private final UserPersistence userPersistence;
    private final PasswordEncoder encoder;
    private final CreateUserValidator createUserValidator;

    public UserService(UserPersistence userPersistence, PasswordEncoder encoder, CreateUserValidator createUserValidator) {
        Objects.requireNonNull(userPersistence);
        Objects.requireNonNull(encoder);
        Objects.requireNonNull(createUserValidator);
        this.userPersistence = userPersistence;
        this.encoder = encoder;
        this.createUserValidator = createUserValidator;
    }

    @Override
    public CreateUserOutput create(CreateUserInput createUserInput) throws DomainException {
        Objects.requireNonNull(createUserInput);
        var user = createUserInput.toDomain();
        createUserValidator.validate(user);
        var encodedUser = new User(user.name(), user.email(), user.login(), encoder.encode(user.password()), user.address(), user.roles());
        var createdUser = userPersistence.create(encodedUser);
        return new CreateUserOutput(createdUser);
    }

    @Override
    public Optional<GetUserOutput> findByUuid(UUID uuid) {
        Objects.requireNonNull(uuid);
        return userPersistence.findByUuid(uuid).map(GetUserOutput::new);
    }

    @Override
    public Optional<GetUserOutput> findEmail(String email) {
        Objects.requireNonNull(email);
        return userPersistence.findByEmail(email).map(GetUserOutput::new);
    }

    @Override
    public Optional<GetUserOutput> findByLogin(String login) {
        Objects.requireNonNull(login);
        return userPersistence.findByLogin(login).map(GetUserOutput::new);
    }
}
