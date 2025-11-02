package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.update.UpdateUserInput;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "Payload para alteração dos dados do usuário.", name = "UserUpdateRequest")
public record UserUpdateForm (
        @NotBlank
        @Size(min = 3,max = 120)
        @Schema(example = "Maria Silva")
        String name,
        @NotBlank
        @Email
        @Size(min = 5, max = 180)
        @Schema(example = "maria.silva@mail.com")
        String email,
        @NotBlank
        @Size(max = 80)
        @Schema(example = "mariasilva")
        String login,
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
) {
    public UpdateUserInput toUpdateUserInput(UUID uuid) {
        return new UpdateUserInput(
            uuid,
            name,
            email,
            login,
            address != null ? address().toUpdateAddressInput() : null,
            roles == null || roles.isEmpty()
                ? Set.of(RoleForm.CUSTOMER.toUpdateRoleInput())
                : roles.stream().map(RoleForm::toUpdateRoleInput).collect(Collectors.toSet())
        );
    }
}
