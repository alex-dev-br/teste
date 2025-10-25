package br.com.fiap.restaurantusersapi.api.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateRoleInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.RoleEntity;
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
        @Size(max = 120)
        @Schema(example = "Maria Silva")
        String name,

        @NotBlank
        @Email
        @Size(max = 180)
        @Schema(example = "maria.silva@mail.com")
        String email,

        @NotBlank
        @Size(max = 80)
        @Schema(example = "mariasilva")
        String login,

        @NotBlank
        @Schema(example = "Senha Fort3!@#*")
        String password,

        @Valid
        @Schema(implementation = AddressForm.class)
        AddressForm address,

        @ArraySchema(
                schema = @Schema(implementation = RoleEntity.class),
                arraySchema = @Schema(
                        description = "Lista de papéis do usuário. Se não for informada, o papel padrão 'CLIENT' será atribuído." ,
                        example = "[\"CLIENT\", \"OWNER\", \"ADMIN\"]"
                ),
                uniqueItems = true
        )
        Set<RoleEntity> roles
){
    public CreateUserInput toCreateUserInput() {
        return new CreateUserInput(
                name,
                email,
                login,
                password,
                address != null ? address.toCreateAddressInput() : null,
                roles.stream().map(r -> new CreateRoleInput(r.name())).collect(Collectors.toSet())
        );
    }
}
