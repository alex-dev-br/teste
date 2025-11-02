package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public class AdminUserCreateForm extends CustomerUserCreateForm {

    @ArraySchema(
            arraySchema = @Schema(
                    description = "Lista de papéis do usuário. Se não for informada, o papel padrão 'CUSTOMER' será atribuído." ,
                    example = "[\"CUSTOMER\", \"OWNER\", \"ADMIN\"]",
                    implementation = RoleForm.class
            ),
            uniqueItems = true
    )
    private final Set<RoleForm> roles;

    public AdminUserCreateForm(String name, String email, String login, String password, AddressForm address, Set<RoleForm> roles) {
        super(name, email, login, password, address);
        this.roles = roles;
    }

    public Set<RoleForm> getRoles() {
        return roles;
    }
}
