package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.security;

import br.com.fiap.restaurantusersapi.application.domain.user.Password;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl implements br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder {

    private final PasswordEncoder encoder;

    public PasswordEncoderImpl(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public Password encode(Password password) {
        return new Password(encoder.encode(password.value()), true);
    }
}
