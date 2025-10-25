package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    @DisplayName("Deve criar um email quando o endereço é válido")
    void shouldCreateEmailWhenAddressIsValid() {
        var validEmail = "test@example.com";
        var email = assertDoesNotThrow(() -> new Email(validEmail));
        assertThat(email.address(), is(equalTo(validEmail)));
    }

    @Test
    @DisplayName("Deve criar um email em minusculo quando o endereço é válido")
    void shouldCreateEmailInLowerCaseWhenAddressIsValid() {
        var validEmail = "TEST@example.com";
        var email = assertDoesNotThrow(() -> new Email(validEmail));
        assertThat(email.address(), is(equalTo(validEmail.toLowerCase())));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o endereço é inválido")
    void shouldThrowExceptionWhenAddressIsInvalid() {
        var invalidEmail = "invalid-email";
        var exception = assertThrows(DomainException.class, () -> new Email(invalidEmail));
        assertThat(exception.getMessage(), is("Invalid email address format"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o endereço está vazio")
    void shouldThrowExceptionWhenAddressIsEmpty() {
        var emptyEmail = "";
        var exception = assertThrows(DomainException.class, () -> new Email(emptyEmail));
        assertThat(exception.getMessage(), is("Invalid email address format"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o endereço é nulo")
    void shouldThrowExceptionWhenAddressIsNull() {
        var exception = assertThrows(DomainException.class, () -> new Email(null));
        assertThat(exception.getMessage(), is("Invalid email address format"));
    }

    @Test
    @DisplayName("Deve lançar exceção para email sem domínio")
    void shouldThrowExceptionForEmailWithoutDomain() {
        var emailWithoutDomain = "test@";
        var exception = assertThrows(DomainException.class, () -> new Email(emailWithoutDomain));
        assertThat(exception.getMessage(), is("Invalid email address format"));
    }

    @Test
    @DisplayName("Deve lançar exceção para email sem o símbolo @")
    void shouldThrowExceptionForEmailWithoutAtSymbol() {
        String emailWithoutAt = "testexample.com";
        var exception = assertThrows(DomainException.class, () -> new Email(emailWithoutAt));
        assertThat(exception.getMessage(), is("Invalid email address format"));
    }
}
