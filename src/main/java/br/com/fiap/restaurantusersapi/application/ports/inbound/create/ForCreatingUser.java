package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

public interface ForCreatingUser {
    CreateUserOutput create(CreateUserInput createUserInput) throws DomainException;
}
