package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

public record PasswordChangeDTO (
        String currentPassword,
        String newPassword
) {}