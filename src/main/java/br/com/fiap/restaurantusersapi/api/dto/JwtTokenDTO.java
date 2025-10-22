package br.com.fiap.restaurantusersapi.api.dto;

import br.com.fiap.restaurantusersapi.domain.JwtToken;

public record JwtTokenDTO(String accessToken, String tokeType, Long expiresIn) {
    public JwtTokenDTO(JwtToken token) {
        this(token.accessToken(), token.tokenType(), token.expiresIn());
    }
}
