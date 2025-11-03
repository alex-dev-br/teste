package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

public record JwtTokenDTO(String accessToken, String tokenType, Long expiresIn) {
    public JwtTokenDTO(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
