package br.com.fiap.restaurantusersapi.application.ports.outbound.persistence;

import br.com.fiap.restaurantusersapi.application.domain.user.User;

public interface SaveUserWithPolicyPort {
    User saveAdminWithPolicy(User user, boolean forceChangeOnFirstLogin);
}
