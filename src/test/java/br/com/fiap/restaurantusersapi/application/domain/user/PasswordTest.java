package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordTest {

    @Test
    @DisplayName("Deve criar uma senha quando ela é forte e válida")
    void shouldCreatePasswordWhenItIsStrongAndValid() {
        var strongPassword = "Password@123";
        var password = assertDoesNotThrow(() -> new Password(strongPassword));
        assertThat(password.value(), is(equalTo(strongPassword)));
        assertThat(password.encoded(), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "weak",           // too short
            "nouppercase1@",  // no uppercase
            "NOLOWERCASE1@",  // no lowercase
            "NoNumber@",      // no number
            "NoSpecial1",     // no special char
            "Short@1"         // too short
    })
    @DisplayName("Deve lançar exceção quando a senha é fraca")
    void shouldThrowExceptionWhenPasswordIsWeak(String weakPassword) {
        var exception = assertThrows(DomainException.class, () -> new Password(weakPassword));
        assertThat(exception.getMessage(), is(Password.PASSWORD_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha é nula")
    void shouldThrowExceptionWhenPasswordIsNull() {
        var exception = assertThrows(DomainException.class, () -> new Password(null));
        assertThat(exception.getMessage(), is(Password.NULL_OR_EMPTY_MESSAGE));
    }

    @Test
    @DisplayName("Deve criar uma senha sem validar quando ela já está encriptada")
    void shouldCreatePasswordWithoutValidationWhenAlreadyEncrypted() {
        var encryptedPassword = "$2a$10$abcdefghijklmnopqrstuv"; // Example of a bcrypt hash
        var password = assertDoesNotThrow(() -> new Password(encryptedPassword, true));
        assertThat(password.value(), is(equalTo(encryptedPassword)));
        assertThat(password.encoded(), is(true));
    }

    @Test
    @DisplayName("Não deve lançar exceção para senha fraca quando ela está marcada como encriptada")
    void shouldNotThrowExceptionForWeakPasswordWhenMarkedAsEncrypted() {
        String weakPassword = "weak";
        var password = assertDoesNotThrow(() -> new Password(weakPassword, true));
        assertThat(password.value(), is(equalTo(weakPassword)));
        assertThat(password.encoded(), is(true));
    }
}
