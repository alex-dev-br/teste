package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

public interface ForCreatingUser {
    CreateUserOut create(CreateUserInput createUserInput) throws DomainException;
}
