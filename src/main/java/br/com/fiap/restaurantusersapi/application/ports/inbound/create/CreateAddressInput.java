package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;
import br.com.fiap.restaurantusersapi.application.domain.user.Address;
import br.com.fiap.restaurantusersapi.application.ports.ToDomainMapper;


public record CreateAddressInput(String street, String number, String complement, String city, String neighborhood, String state, String zipCode) implements ToDomainMapper<Address> {

    @Override
    public Address toDomain() throws DomainException {
        return new Address(
            this.street, this.number, this.complement, this.city, this.neighborhood, this.state, this.zipCode
        );
    }
}
