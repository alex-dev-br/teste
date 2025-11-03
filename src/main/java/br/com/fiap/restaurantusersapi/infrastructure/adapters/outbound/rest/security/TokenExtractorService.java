package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class TokenExtractorService {

    /**
     * Extrai o token do header Authorization da requisição HTTP.
     * Aceita apenas o esquema "Bearer" (case-insensitive).
     * @return o token (string não vazia) ou null se ausente/malformado.
     */
    public String extractRequestToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return extractRequestToken(header);
    }

    /**
     * Extrai o token de uma string no formato "Bearer <token>".
     * Retorna null para entradas nulas, vazias, com esquema diferente de Bearer,
     * ou sem a segunda parte (o token em si).
     */
    public String extractRequestToken(String tokenAndTypeToken) {
        if (tokenAndTypeToken == null) {
            return null;
        }

        String trimmed = tokenAndTypeToken.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // Divide por qualquer quantidade de espaço em branco
        String[] parts = trimmed.split("\\s+");
        if (parts.length < 2) {
            return null; // evita AIOOBE e headers do tipo "Bearer" sem token
        }

        String scheme = parts[0];
        if (!"Bearer".equalsIgnoreCase(scheme)) {
            return null; // rejeita "Basic", "Digest", etc.
        }

        String token = parts[1].trim();
        if (token.isEmpty()) {
            return null;
        }

        return token;
    }
}
