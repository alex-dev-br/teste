package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.update.UpdateUserInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Payload para alteração dos dados do usuário.", name = "UserUpdateRequest")
public final class AdminUserUpdateForm extends UserUpdateForm {

    @ArraySchema(
            arraySchema = @Schema(
                    description = "Lista de papéis do usuário. Se não for informada, o papel padrão 'CUSTOMER' será atribuído." ,
                    example = "[\"CUSTOMER\", \"OWNER\", \"ADMIN\"]",
                    implementation = RoleForm.class
            ),
            uniqueItems = true
    )
    private final Set<RoleForm> roles;

    public AdminUserUpdateForm(String name, String email, String login, AddressForm address, Set<RoleForm> roles) {
        super(name, email, login, address);
        this.roles = roles;
    }

    @Operation
    public UpdateUserInput toUpdateUserInput(UUID uuid) {
        return this.toUpdateUserInput(uuid, roles);
    }
}
