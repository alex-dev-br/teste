package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.domain;

public record JwtToken(String accessToken, String tokenType, Long expiresIn) {
    public JwtToken(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
