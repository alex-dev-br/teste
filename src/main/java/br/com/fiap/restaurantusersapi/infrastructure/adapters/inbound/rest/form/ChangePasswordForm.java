package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.update.password.ChangeUserPasswordInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Payload para alteração de senha do usuário.", name = "ChangePasswordRequest")
public record ChangePasswordForm (
        @NotBlank
        @Schema(example = "S&nh4F0rt3")
        String currentPassword,
        @Schema(example = "N0v4S&nh@")
        @NotBlank String newPassword,
        @Schema(example = "N0v4S&nh@")
        @NotBlank String confirmNewPassword
) {
    public ChangeUserPasswordInput toChangePasswordInput(UUID uuid) {
        return new ChangeUserPasswordInput(uuid, currentPassword, newPassword, confirmNewPassword);
    }
}