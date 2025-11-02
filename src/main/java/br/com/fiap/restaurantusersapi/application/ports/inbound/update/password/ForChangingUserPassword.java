package br.com.fiap.restaurantusersapi.application.ports.inbound.update.password;

public interface ForChangingUserPassword {
    void changeUserPassword(ChangeUserPasswordInput input);
}
