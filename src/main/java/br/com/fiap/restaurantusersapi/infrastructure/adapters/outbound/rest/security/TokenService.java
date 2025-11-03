package br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.dto.JwtTokenDTO;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final String secretBase64;
    private final long expirationSeconds;

    // Cache da chave para evitar recriação a cada chamada
    private SecretKey key;

    public TokenService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.expiration}") Long expiration) {
        this.secretBase64 = secret;
        this.expirationSeconds = (expiration == null ? 3600L : expiration);
    }

    @PostConstruct
    void initKey() {
        byte[] secretBytes = Decoders.BASE64.decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public JwtTokenDTO generateToken(Authentication authentication) {
        var principal = (UserEntity) authentication.getPrincipal();

        // Sempre UTC; evita hacks de ZoneOffset(-3)
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        String accessToken = Jwts.builder()
                .issuer("Restaurant-API")
                .subject(principal.getUuid().toString())
                .claim("pwdv", principal.getPwdVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();

        return new JwtTokenDTO(accessToken, expirationSeconds);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) { // ExpiredJwtException, SecurityException, MalformedJwtException etc.
            return false;
        }
    }

    public UUID getUserId(String token) throws UnsupportedJwtException {
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return UUID.fromString(subject);
    }

    // Versão do password embutida no token (para revogação por mudança de senha)
    public int getPasswordVersion(String token) {
        try {
            var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Number n = claims.get("pwdv", Number.class);
            return n == null ? -1 : n.intValue();
        } catch (Exception e) {
            return -1;
        }
    }

    // Utilitário para gerar segredos base64 para testes locais
    public static void main(String[] args) {
        MacAlgorithm alg = Jwts.SIG.HS512; // ou HS384 / HS256
        System.out.println(Encoders.BASE64.encode(alg.key().build().getEncoded()));
    }
}
