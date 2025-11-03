package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.domain.exception.BusinessValidationException;
import br.com.fiap.restaurantusersapi.application.domain.exception.CurrentPasswordMismatchException;
import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;
import br.com.fiap.restaurantusersapi.application.domain.user.Password;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.ForCreatingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.delete.ForDeletingByUuid;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.ForGettingUser;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.GetUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ForListingUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ListUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.UpdateUserInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.UpdateUserOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.password.ForChangingUserPassword;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.password.ChangeUserPasswordInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.ForUpdatingUser;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;
import br.com.fiap.restaurantusersapi.application.service.validator.CreateUserValidator;
import br.com.fiap.restaurantusersapi.application.service.validator.UpdateUserValidator;
import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Named
public class UserService implements
        ForCreatingUser,
        ForGettingUser,
        ForListingUserOutput,
        ForDeletingByUuid,
        ForChangingUserPassword,
        ForUpdatingUser {

    private final UserPersistence userPersistence;
    private final PasswordEncoder encoder;
    private final CreateUserValidator createUserValidator;
    private final UpdateUserValidator updateUserValidator;

    public UserService(UserPersistence userPersistence,
                       PasswordEncoder encoder,
                       CreateUserValidator createUserValidator,
                       UpdateUserValidator updateUserValidator) {
        this.updateUserValidator = updateUserValidator;
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
        var result = createUserValidator.validate(user);
        if (result.isInvalid()) {
            throw new BusinessValidationException(result);
        }
        var encodedUser = new User(
                user.name(),
                user.email(),
                user.login(),
                encoder.encode(user.password()),
                user.address(),
                user.roles()
        );
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

    @Override
    public Pagination<ListUserOutput> findByName(String name, Page page) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(page);
        return userPersistence.findByName(name, page).mapItems(ListUserOutput::new);
    }

    @Override
    @Transactional
    public void changeUserPassword(ChangeUserPasswordInput input) {
        var userCurrentPassword = userPersistence.getUserPassword(input.userUuid())
                .orElseThrow(() -> new DomainException("Usuário com inconsistência"));

        if (!input.newPassword().equals(input.confirmNewPassword())) {
            throw new BusinessValidationException(new ValidationResult(
                    "A nova senha não confere com a confirmação de nova senha"));
        }

        if (!encoder.matches(input.currentPassword(), userCurrentPassword)) {
            throw new CurrentPasswordMismatchException();
        }

        var newPasswordEncoded = encoder.encode(new Password(input.newPassword()));
        userPersistence.changePassword(input.userUuid(), newPasswordEncoded.value());
    }

    @Override
    @Transactional
    public UpdateUserOutput update(UpdateUserInput form) {
        var user = form.toDomain();
        var result = updateUserValidator.validate(user);
        if (result.isInvalid()) {
            throw new BusinessValidationException(result);
        }
        return new UpdateUserOutput(userPersistence.update(user));
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        Objects.requireNonNull(uuid);
        userPersistence.deleteByUuid(uuid);
    }
}
