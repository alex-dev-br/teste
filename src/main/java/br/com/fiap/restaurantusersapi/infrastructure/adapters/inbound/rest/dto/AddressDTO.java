package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateAddressOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.get.GetAddressOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.list.ListAddressOutput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.UpdateAddressOutput;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.AddressEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação de endereço", name = "AddressResponse")
public record AddressDTO (
    @Schema(example = "Rua das Flores") String street,
    @Schema(example = "123") String number,
    @Schema(example = "Apto 101") String complement,
    @Schema(example = "São Paulo") String city,
    @Schema(example = "Pinheiros") String neighborhood,
    @Schema(example = "SP", minLength = 2, maxLength = 2) String state,
    @Schema(example = "12345-678", minLength = 8, maxLength = 9) String zipCode
) {
    public AddressDTO(AddressEntity address) {
        this(address.getStreet(), address.getNumber(), address.getComplement(), address.getCity(), address.getNeighborhood(), address.getState(), address.getZipCode());
    }

    public AddressDTO(CreateAddressOutput address) {
        this(address.street(), address.number(), address.complement(), address.city(), address.neighborhood(), address.state(), address.zipCode());
    }

    public AddressDTO(GetAddressOutput address) {
        this(address.street(), address.number(), address.complement(), address.city(), address.neighborhood(), address.state(), address.zipCode());
    }

    public AddressDTO(ListAddressOutput address) {
        this(address.street(), address.number(), address.complement(), address.city(), address.neighborhood(), address.state(), address.zipCode());
    }

    public AddressDTO(UpdateAddressOutput address) {
        this(address.street(), address.number(), address.complement(), address.city(), address.neighborhood(), address.state(), address.zipCode());
    }
}
