package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "Payload para criação de usuário.", name = "UserCreateRequest")
public record UserCreateForm(

        @NotBlank
        @Size(min = 3,max = 120)
        @Schema(example = "Maria Silva")
        String name,

        @NotBlank
        @Email
        @Size(min = 5,max = 180)
        @Schema(example = "maria.silva@mail.com")
        String email,

        @NotBlank
        @Size(max = 80)
        @Schema(example = "mariasilva")
        String login,

        @NotBlank
        @Schema(example = "SenhaFort3!@#*")
        String password,

        @Valid
        @Schema(implementation = AddressForm.class)
        AddressForm address,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "Lista de papéis do usuário. Se não for informada, o papel padrão 'CUSTOMER' será atribuído." ,
                        example = "[\"CUSTOMER\", \"OWNER\", \"ADMIN\"]",
                        implementation = RoleForm.class
                ),
                uniqueItems = true
        )
        Set<RoleForm> roles
){
    public CreateUserInput toCreateUserInput() {
        return new CreateUserInput(
                name,
                email,
                login,
                password,
                address != null ? address.toCreateAddressInput() : null,
                roles == null || roles.isEmpty()
                        ? Set.of(RoleForm.CUSTOMER.toCreateRoleInput())
                        : roles.stream().map(RoleForm::toCreateRoleInput).collect(Collectors.toSet())
        );
    }
}
