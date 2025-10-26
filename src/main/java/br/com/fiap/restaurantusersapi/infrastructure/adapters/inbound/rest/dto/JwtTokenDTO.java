package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.domain.JwtToken;

public record JwtTokenDTO(String accessToken, String tokeType, Long expiresIn) {
    public JwtTokenDTO(JwtToken token) {
        this(token.accessToken(), token.tokenType(), token.expiresIn());
    }
}
