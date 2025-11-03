package br.com.fiap.restaurantusersapi.application.ports.outbound.security;

import br.com.fiap.restaurantusersapi.application.domain.user.Password;

public interface PasswordEncoder {
    Password encode(Password raw);
    boolean matches(String rawPassword, String encodedPassword);
}
