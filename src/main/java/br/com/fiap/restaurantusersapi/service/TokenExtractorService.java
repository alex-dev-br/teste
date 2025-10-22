package br.com.fiap.restaurantusersapi.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class TokenExtractorService {

    public String extractRequestToken(HttpServletRequest request) {
        String tokenAndTypeToken = request.getHeader("Authorization");
        return extractRequestToken(tokenAndTypeToken);
    }

    public String extractRequestToken(String tokenAndTypeToken) {
        if (tokenAndTypeToken == null) {
            return null;
        }

        String[] tokenInfo = tokenAndTypeToken.trim().split(" ");
        return tokenInfo[1] != null ? tokenInfo[1] : null;
    }
}
