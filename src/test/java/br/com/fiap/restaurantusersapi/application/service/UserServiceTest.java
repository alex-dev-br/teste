package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.domain.user.Email;
import br.com.fiap.restaurantusersapi.application.domain.user.Password;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateAddressInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateRoleInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateRoleOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;
import br.com.fiap.restaurantusersapi.application.service.validator.CreateUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserPersistence userPersistence;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userPersistence = mock(UserPersistence.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userPersistence, passwordEncoder, new CreateUserValidator(List.of()));
    }

    @Test
    void shouldCreateNewUserWithSuccess() {
        var addressInput = new CreateAddressInput (
            "rua das flores", "60B", "Casa de verde", "São Paulo", "Pinheiros", "SP", "01234-567"
        );
        var userInput = new CreateUserInput (
                "Ana Paula Silva",
                "ana.silva@mail.com",
                "ana.silva",
                "P4$$w0rd",
                addressInput,
                Set.of(new CreateRoleInput("OWNER"))
        );

        Password encodedPassword = new Password("encodedPassword", true);
        User createdUser = new User(userInput.name(), new Email(userInput.email()), userInput.login(), encodedPassword, addressInput.toDomain(), Set.of(new CreateRoleInput("OWNER").toDomain()));

        when(passwordEncoder.encode(any(Password.class))).thenReturn(encodedPassword);
        when(userPersistence.create(any(User.class))).thenReturn(createdUser);

        var userOutput = userService.create(userInput);

        assertThat(userOutput, is(notNullValue()));
        assertThat(userOutput.uuid(), is(notNullValue()));
        assertThat(userOutput.name(), is(equalTo(userInput.name())));
        assertThat(userOutput.email(), is(equalTo(userInput.email())));
        assertThat(userOutput.login(), is(equalTo(userInput.login())));
        assertThat(userOutput.roles(), hasSize(1));
        assertThat(userOutput.roles(), contains(new CreateRoleOutput("OWNER")));
        assertThat(userOutput.createdAt(), is(notNullValue()));
        assertThat(userOutput.updatedAt(), is(notNullValue()));

        var addressOutput = userOutput.address();
        assertThat(addressOutput, is(notNullValue()));
        assertThat(addressOutput.street(), is(equalTo(addressInput.street())));
        assertThat(addressOutput.number(), is(equalTo(addressInput.number())));
        assertThat(addressOutput.complement(), is(equalTo(addressInput.complement())));
        assertThat(addressOutput.city(), is(equalTo(addressInput.city())));
        assertThat(addressOutput.neighborhood(), is(equalTo(addressInput.neighborhood())));
        assertThat(addressOutput.state(), is(equalTo(addressInput.state())));
        assertThat(addressOutput.zipCode(), is(equalTo(addressInput.zipCode())));

        verify(passwordEncoder).encode(any(Password.class));
        verify(userPersistence).create(any(User.class));
    }

    @Test
    void shouldCreateNewUserWithoutAddressWithSuccess() {
        var userInput = new CreateUserInput (
                "Ana Paula Silva",
                "ana.silva@mail.com",
                "ana.silva",
                "P4$$w0rd",
                null,
                Set.of(new CreateRoleInput("OWNER"))
        );

        Password encodedPassword = new Password("encodedPassword", true);
        User createdUser = new User(userInput.name(), new Email(userInput.email()), userInput.login(), encodedPassword, null, Set.of(new CreateRoleInput("OWNER").toDomain()));

        when(passwordEncoder.encode(any(Password.class))).thenReturn(encodedPassword);
        when(userPersistence.create(any(User.class))).thenReturn(createdUser);

        var userOutput = userService.create(userInput);

        assertThat(userOutput, is(notNullValue()));
        assertThat(userOutput.uuid(), is(notNullValue()));
        assertThat(userOutput.name(), is(equalTo(userInput.name())));
        assertThat(userOutput.email(), is(equalTo(userInput.email())));
        assertThat(userOutput.login(), is(equalTo(userInput.login())));
        assertThat(userOutput.roles(), hasSize(1));
        assertThat(userOutput.roles(), contains(new CreateRoleOutput("OWNER")));
        assertThat(userOutput.createdAt(), is(notNullValue()));
        assertThat(userOutput.updatedAt(), is(notNullValue()));

        var addressOutput = userOutput.address();
        assertThat(addressOutput, is(nullValue()));

        verify(passwordEncoder).encode(any(Password.class));
        verify(userPersistence).create(any(User.class));
    }
}