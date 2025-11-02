package br.com.fiap.restaurantusersapi.application.ports.inbound.update;

public interface ForUpdatingUser {
    UpdateUserOutput update(UpdateUserInput input);
}
