package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateAddressInput;

public record UserUpdateForm(
        String name,
        String email,
        String login,
        CreateAddressInput address
) {}
