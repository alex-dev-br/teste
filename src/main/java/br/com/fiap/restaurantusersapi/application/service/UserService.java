package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.domain.exception.BusinessValidationException;
import br.com.fiap.restaurantusersapi.application.domain.exception.CurrentPasswordMismatchException;
import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Page;
import br.com.fiap.restaurantusersapi.application.domain.pagination.Pagination;
import br.com.fiap.restaurantusersapi.application.domain.user.Email;
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
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;
import br.com.fiap.restaurantusersapi.application.service.validator.CreateUserValidator;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form.ChangePasswordForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.UserUpdateForm;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Named
public class UserService implements ForCreatingUser, ForGettingUser, ForListingUserOutput, ForDeletingByUuid {

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
        var result = createUserValidator.validate(user);
        if (result.isInvalid()) {
            throw new BusinessValidationException(result);
        }
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

    @Override
    public Pagination<ListUserOutput> findByName(String name, Page page) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(page);
        return userPersistence.findByName(name, page).mapItems(ListUserOutput::new);
    }

    @Transactional
    public void changePassword(UserDetails userDetails, ChangePasswordForm form) {
        Objects.requireNonNull(userDetails);
        Objects.requireNonNull(form);
        var user = (UserEntity) userDetails;

        if (!encoder.matches(form.currentPassword(), user.getPassword())) {
            throw new CurrentPasswordMismatchException();
        }

        var newPasswordEncoded = encoder.encode(new Password(form.newPassword(), false));
        userPersistence.changePassword(user.getId(), newPasswordEncoded.value());
    }

    @Transactional
    public GetUserOutput update(UUID uuid, UserUpdateForm form) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var authenticatedLogin = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));

        var user = userPersistence.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAdmin && !authenticatedLogin.equalsIgnoreCase(user.login())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        var updated = new User(
                user.uuid(),
                form.name() != null ? form.name() : user.name(),
                new Email(form.email() != null ? form.email() : user.email().address()),
                form.login() != null ? form.login() : user.login(),
                user.password(),
                form.address() != null ? form.address().toDomain() : user.address(),
                user.roles(),
                user.createdAt(),
                Instant.now()
        );

        return new GetUserOutput(userPersistence.create(updated));
    }


    @Override
    public void deleteByUuid(UUID uuid) {
        Objects.requireNonNull(uuid);
        userPersistence.deleteByUuid(uuid);
    }
}
