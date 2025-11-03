package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class TokenExtractorService {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    public String extractRequestToken(HttpServletRequest request) {
        return extractRequestToken(request.getHeader(AUTH_HEADER));
    }

    public String extractRequestToken(String headerValue) {
        if (headerValue == null) return null;

        String h = headerValue.trim();
        if (h.isEmpty()) return null;

        int space = h.indexOf(' ');
        if (space <= 0) return null; // sem esquema ou sem espaço

        String scheme = h.substring(0, space).trim();
        String token  = h.substring(space + 1).trim();

        if (!BEARER.equalsIgnoreCase(scheme)) return null;
        if (token.isEmpty()) return null;

        return token;
    }
}
