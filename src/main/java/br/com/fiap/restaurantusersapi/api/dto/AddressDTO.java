package br.com.fiap.restaurantusersapi.api.dto;

import br.com.fiap.restaurantusersapi.domain.Address;
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
    public AddressDTO(Address address) {
        this(address.getStreet(), address.getNumber(), address.getComplement(), address.getCity(), address.getNeighborhood(), address.getState(), address.getZipCode());
    }
}
