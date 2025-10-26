package br.com.fiap.restaurantusersapi.application.ports.inbound.get;

import br.com.fiap.restaurantusersapi.application.domain.user.Address;

public record GetAddressOutput(Long id, String street, String number, String complement, String city, String neighborhood, String state, String zipCode) {
    public GetAddressOutput(Address address) {
        this(address.id(), address.street(), address.number(), address.complement(), address.city(), address.neighborhood(), address.state(), address.zipCode());
    }
}
