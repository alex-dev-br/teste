package br.com.fiap.restaurantusersapi.application.ports.inbound.delete;

import java.util.UUID;

public interface ForDeletingByUuid {
    void deleteByUuid(UUID uuid);
}
