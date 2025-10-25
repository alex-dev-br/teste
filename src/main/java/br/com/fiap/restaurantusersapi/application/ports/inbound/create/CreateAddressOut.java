package br.com.fiap.restaurantusersapi.application.ports.inbound.create;

import br.com.fiap.restaurantusersapi.application.domain.user.Address;

public record CreateAddressOut(Long id, String street, String number, String complement, String city, String neighborhood, String state, String zipCode) {
    public CreateAddressOut(Address address) {
        this(address.id(), address.street(), address.number(), address.complement(), address.city(), address.neighborhood(), address.state(), address.zipCode());
    }
}
