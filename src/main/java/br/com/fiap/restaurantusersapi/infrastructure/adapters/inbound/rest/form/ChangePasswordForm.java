package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload para alteração de senha do usuário.", name = "ChangePasswordRequest")
public record ChangePasswordForm (
        @NotBlank
        @Schema(example = "S&nh4F0rt3")
        String currentPassword,
        @Schema(example = "N0v4S&nh@")
        @NotBlank String newPassword,
        @Schema(example = "N0v4S&nh@")
        @NotBlank String confirmNewPassword
) {}