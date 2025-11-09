package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    private static final String PASSWORD_ERROR_MESSAGE = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&).";

    @Test
    @DisplayName("Deve criar um usuário quando todos os dados são válidos")
    void shouldCreateUserWhenDataIsValid() {
        assertDoesNotThrow(() -> new User(
                "Test User",
                new Email("test@example.com"),
                "testuser",
                new Password("Password@123"),
                null, // Address can be null
                Set.of(new Role("CUSTOMER"))
        ));
    }

    @Test
    @DisplayName("Deve criar um usuário com login em caixa baixa quando todos os dados são válidos")
    void shouldCreateUserWithLoginInLowerCaseWhenDataIsValid() {
        var login = "TESTE";
        var user = assertDoesNotThrow(() -> new User(
                "Test User",
                new Email("test@example.com"),
                login,
                new Password("Password@123"),
                null, // Address can be null
                Set.of(new Role("CUSTOMER"))
        ));

        assertThat(user, is(notNullValue()));
        assertThat(user.login(), is(equalTo(login.toLowerCase())));
    }

    @Test
    @DisplayName("Deve criar um usuário com UUID quando todos os dados são válidos")
    void shouldCreateUserWithUuuidCaseWhenDataIsValid() {
        var user = assertDoesNotThrow(() -> new User(
                "Test User",
                new Email("test@example.com"),
                "TESTE",
                new Password("Password@123"),
                null, // Address can be null
                Set.of(new Role("CUSTOMER"))
        ));

        assertThat(user, is(notNullValue()));
        assertThat(user.uuid(), is(notNullValue()));
    }

    @Test
    @DisplayName("Deve criar um usuário com data de inclusão e atualização quando todos os dados são válidos")
    void shouldCreateUserWithCreateAtAndUpdatedAtCaseWhenDataIsValid() {
        var user = assertDoesNotThrow(() -> new User(
                "Test User",
                new Email("test@example.com"),
                "TESTE",
                new Password("Password@123"),
                null, // Address can be null
                Set.of(new Role("CUSTOMER"))
        ));

        assertThat(user, is(notNullValue()));
        assertThat(user.createdAt(), is(notNullValue()));
        assertThat(user.updatedAt(), is(notNullValue()));
        assertThat(user.createdAt(), is(equalTo(user.updatedAt())));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome é nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        var exception = assertThrows(DomainException.class, () -> new User(null, new Email("test@example.com"), "testuser", new Password("Password@123"), null, Set.of(new Role("CUSTOMER"))));
        assertThat(exception.getMessage(), is("Name cannot be null or empty"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email é nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        var exception = assertThrows(DomainException.class, () -> new User("Test User", null, "testuser", new Password("Password@123"), null, Set.of(new Role("CUSTOMER"))));
        assertThat(exception.getMessage(), is("Email cannot be null"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o login é nulo")
    void shouldThrowExceptionWhenLoginIsNull() {
        var exception = assertThrows(DomainException.class, () -> new User("Test User", new Email("test@example.com"), null, new Password("Password@123"), null, Set.of(new Role("CUSTOMER"))));
        assertThat(exception.getMessage(), is("Login cannot be null or empty"));
    }
    
}
