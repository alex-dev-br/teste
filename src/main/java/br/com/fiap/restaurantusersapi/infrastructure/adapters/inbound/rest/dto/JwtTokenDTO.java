package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação do Token de autenticação", name = "JwtBearerTokenResponse")
public record JwtTokenDTO(String accessToken, String tokeType, Long expiresIn) {
    public JwtTokenDTO(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
