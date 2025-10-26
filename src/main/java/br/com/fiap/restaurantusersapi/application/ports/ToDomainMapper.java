package br.com.fiap.restaurantusersapi.application.ports;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

public interface ToDomainMapper<T> {
    T toDomain() throws DomainException;
}
