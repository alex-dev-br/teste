package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Schema(description = "Representação de formulário de login", name = "LoginRequest")
public record LoginForm(@NotBlank String login, @NotBlank String password) {
    public UsernamePasswordAuthenticationToken toAuthenticatorToken() {
        return new UsernamePasswordAuthenticationToken(this.login, this.password);
    }
}
