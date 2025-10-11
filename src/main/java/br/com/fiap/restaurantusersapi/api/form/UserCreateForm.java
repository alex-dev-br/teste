package br.com.fiap.restaurantusersapi.api.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload para criação de usuário.", name = "UserCreateRequest")
public record UserCreateForm(
    @NotBlank @Schema(example = "Maria Silva") String name,
    @NotBlank @Email @Schema(example = "maria.silva@mail.com") String email,
    @NotBlank @Schema(example = "mariasilva") String login,
    @NotBlank @Schema(example = "Senha Fort3!@#*") String password,
    @NotBlank @Schema(implementation = AddressForm.class) AddressForm address
){}
