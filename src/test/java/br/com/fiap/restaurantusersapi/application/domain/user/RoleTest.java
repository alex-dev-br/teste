package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RoleTest {

    @Test
    @DisplayName("Deve criar um Role quando o nome é válido")
    void shouldCreateRoleWhenNameIsValid() {
        var validName = "ADMIN";
        var role = assertDoesNotThrow(() -> new Role(validName));
        assertThat(role.name(), is(equalTo(validName)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome está em branco")
    void shouldThrowExceptionWhenNameIsBlank() {
        var blankName = "   ";
        var exception = assertThrows(DomainException.class, () -> new Role(blankName));
        assertThat(exception.getMessage(), is("Role name cannot be null or empty"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome é nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        var exception = assertThrows(DomainException.class, () -> new Role(null));
        assertThat(exception.getMessage(), is("Role name cannot be null or empty"));
    }
}
