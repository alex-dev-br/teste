package br.com.fiap.restaurantusersapi.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;


@Schema(description = "Representação de usuário", name = "UserResponse")
public record UserDTO(
    @Schema(example = "2d4c0a1b-9c85-4d1e-9b6a-0a4e2db5b1f7") UUID id,
    @Schema(example = "Maria Silva") String name,
    @Schema(example = "maria.silva@mail.com") String email,
    @Schema(example = "mariasilva") String login,
    @Schema(example = "CLIENT", description = "OWNER ou CLIENT") String role,
    @Schema(example = "2025-09-28T12:34:56Z") Instant lastModifiedAt,
    @Schema(implementation = AddressDTO.class) AddressDTO address   // Modelar Address depois
){}
