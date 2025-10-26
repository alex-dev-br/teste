package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateAddressInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Schema para endereço", name = "AddressRequest")
public record AddressForm(

        @NotBlank
        @Size(max = 255)
        @Schema(example = "Rua das Flores")
        String street,

        @Size(max = 20)
        @Schema(example = "123")
        String number,

        @Size(max = 100)
        @Schema(example = "Apto 101")
        String complement,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "São Paulo")
        String city,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "Pinheiros")
        String neighborhood,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{2}$", message = "O estado deve conter exatamente duas letras maiúsculas")
        @Schema(example = "SP", minLength = 2, maxLength = 2)
        String state,

        @NotBlank
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "O CEP deve estar no formato 00000-000 (traço opcional)")
        @Schema(example = "12345-678", minLength = 8, maxLength = 9)
        String zipCode
) {
    public CreateAddressInput toCreateAddressInput() {
        return new CreateAddressInput(
            street,
            number,
            complement,
            city,
            neighborhood,
            state,
            zipCode
        );
    }
}
