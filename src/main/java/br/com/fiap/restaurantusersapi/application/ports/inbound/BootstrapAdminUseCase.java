package br.com.fiap.restaurantusersapi.application.ports.inbound;

public interface BootstrapAdminUseCase {
    void execute(Command c);

    record Command(String name,
                   String email,
                   String login,
                   String rawPassword,
                   boolean forceChangeOnFirstLogin) {}
}
