package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem;

import org.springframework.security.access.AccessDeniedException;

public class PasswordChangeRequiredException extends AccessDeniedException {

    public PasswordChangeRequiredException() {
        super("Password change required");
    }

}
