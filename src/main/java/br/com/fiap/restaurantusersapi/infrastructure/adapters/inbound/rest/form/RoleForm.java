package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateRoleInput;
import br.com.fiap.restaurantusersapi.application.ports.inbound.update.UpdateRoleInput;

public enum RoleForm {
    CUSTOMER, OWNER, ADMIN;

    public UpdateRoleInput toUpdateRoleInput() {
        return new UpdateRoleInput(this.name());
    }

    public CreateRoleInput toCreateRoleInput() {
        return new CreateRoleInput(this.name());
    }
}
