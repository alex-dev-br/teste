package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserOut;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.ForCreatingUser;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;
import br.com.fiap.restaurantusersapi.application.service.validator.CreateUserValidator;
import jakarta.inject.Named;

import java.util.Objects;

@Named
public class UserService implements ForCreatingUser {

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
    public CreateUserOut create(CreateUserInput createUserInput) throws DomainException {
        Objects.requireNonNull(createUserInput);
        var user = createUserInput.toDomain();
        createUserValidator.validate(user);
        var encodedUser = new User(user.name(), user.email(), user.login(), encoder.encode(user.password()), user.address(), user.roles());
        var createdUser = userPersistence.create(encodedUser);
        return new CreateUserOut(createdUser);
    }
}
