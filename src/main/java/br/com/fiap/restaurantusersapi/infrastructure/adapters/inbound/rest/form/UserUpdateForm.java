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
public class UserUpdateForm {
    @NotBlank
    @Size(min = 3,max = 120)
    @Schema(example = "Maria Silva")
    private final String name;

    @NotBlank
    @Email
    @Size(min = 5, max = 180)
    @Schema(example = "maria.silva@mail.com")
    private final String email;

    @NotBlank
    @Size(max = 80)
    @Schema(example = "mariasilva")
    private final String login;

    @Valid
    @Schema(implementation = AddressForm.class)
    private final AddressForm address;

    public UserUpdateForm(String name, String email, String login, AddressForm address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.address = address;
    }

    /**
     * Atualização do próprio usuário: NÃO altera roles.
     * Envia roles = null para que a camada de persistência preserve os papéis existentes.
     */
    public UpdateUserInput toUpdateUserInput(UUID uuid) {
        return new UpdateUserInput(
                uuid,
                name,
                email,
                login,
                address != null ? address.toUpdateAddressInput() : null,
                null // <- não alterar roles
        );
    }

    /**
     * Método utilitário (ainda usado por AdminUserUpdateForm) caso seja necessário
     * informar papéis explicitamente.
     */
    public UpdateUserInput toUpdateUserInput(UUID uuid, Set<RoleForm> roleForms) {
        return new UpdateUserInput(
                uuid,
                name,
                email,
                login,
                address != null ? address.toUpdateAddressInput() : null,
                roleForms == null ? null : roleForms.stream().map(RoleForm::toUpdateRoleInput).collect(Collectors.toSet())
        );
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getLogin() { return login; }
    public AddressForm getAddress() { return address; }
}
