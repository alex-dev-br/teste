package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public record LoginForm(@NotBlank String login, @NotBlank String password) {
    public UsernamePasswordAuthenticationToken toAuthenticatorToken() {
        return new UsernamePasswordAuthenticationToken(this.login, this.password);
    }
}
