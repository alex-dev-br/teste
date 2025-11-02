package br.com.fiap.restaurantusersapi.application.ports.inbound.update.password;

import java.util.UUID;

public record ChangeUserPasswordInput(UUID userUuid, String currentPassword, String newPassword, String confirmNewPassword) {}
