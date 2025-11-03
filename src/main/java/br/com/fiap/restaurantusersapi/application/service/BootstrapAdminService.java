package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.domain.user.Email;
import br.com.fiap.restaurantusersapi.application.domain.user.Password;
import br.com.fiap.restaurantusersapi.application.domain.user.Role;
import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.inbound.BootstrapAdminUseCase;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.CountAdminsPort;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.SaveUserWithPolicyPort;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;

public class BootstrapAdminService implements BootstrapAdminUseCase {

    private final CountAdminsPort countAdmins;
    private final SaveUserWithPolicyPort saveUserWithPolicy;
    private final PasswordEncoder passwordEncoder; // port (não o bean do Spring)

    public BootstrapAdminService(CountAdminsPort countAdmins,
                                 SaveUserWithPolicyPort saveUserWithPolicy,
                                 PasswordEncoder passwordEncoder) {
        this.countAdmins = countAdmins;
        this.saveUserWithPolicy = saveUserWithPolicy;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(Command c) {
        if (countAdmins.countAdmins() > 0) return; // já existe ADMIN

        var email = new Email(c.email());

        // 1) constrói um Password "cru"
        var rawPassword = Password.raw(c.rawPassword());

        // 2) pede ao port para hashear e devolver um Password "hasheado"
        var hashedPassword = passwordEncoder.encode(rawPassword);

        var roles = java.util.Set.of(Role.ADMIN); // se não tiver constante, use Role.of("ADMIN")

        // 3) usa o Password já hasheado no domínio
        var user = new User(c.name(), email, c.login(), hashedPassword, null, roles);

        saveUserWithPolicy.saveAdminWithPolicy(user, c.forceChangeOnFirstLogin());
    }
}
