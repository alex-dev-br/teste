package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

public record ChangePasswordForm(
        String currentPassword,
        String newPassword
) {}