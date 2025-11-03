package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT de autenticação", name = "JwtTokenResponse")
public record JwtTokenDTO(

        @Schema(example = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(example = "Bearer", defaultValue = "Bearer", description = "Tipo do token (ex.: Bearer)")
        String tokenType,

        @Schema(example = "3600", description = "Tempo de expiração em segundos")
        Long expiresIn
) {
    public JwtTokenDTO(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
