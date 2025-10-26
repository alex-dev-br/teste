package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

public record JwtTokenDTO(String accessToken, String tokeType, Long expiresIn) {
    public JwtTokenDTO(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
