package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.mapper;

import br.com.fiap.restaurantusersapi.application.domain.user.Address;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.AddressEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;

public record AddressMapper() {

    public static AddressEntity toEntity(Address address, UserEntity userEntity) {
        var addressEntity = new AddressEntity();
        addressEntity.setAddressId(address.id());
        addressEntity.setUser(userEntity);
        addressEntity.setStreet(address.street());
        addressEntity.setNumber(address.number());
        addressEntity.setComplement(address.complement());
        addressEntity.setNeighborhood(address.neighborhood());
        addressEntity.setCity(address.city());
        addressEntity.setState(address.state());
        addressEntity.setZipCode(address.zipCode());

        return addressEntity;
    }

    public static Address toDomain(AddressEntity address) {
        return new Address(
                address.getAddressId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getCity(),
                address.getNeighborhood(),
                address.getState(),
                address.getZipCode()
        );
    }
}
