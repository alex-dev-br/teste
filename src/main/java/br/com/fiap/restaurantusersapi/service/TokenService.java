package br.com.fiap.restaurantusersapi.service;

import br.com.fiap.restaurantusersapi.domain.JwtToken;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final String secret;
    private final Long expiration;

    public TokenService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.expiration}") Long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public JwtToken generateToken(Authentication authentication) {
        var principal = (UserEntity) authentication.getPrincipal();

        byte[] decode = Decoders.BASE64.decode(secret);
        var keys = Keys.hmacShaKeyFor(decode);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plus(expiration, TimeUnit.SECONDS.toChronoUnit());

        var accessToken = Jwts.builder()
                .issuer("Restaurant-API")
                .subject(principal.getId().toString())
                .issuedAt(Date.from(now.toInstant(ZoneOffset.ofHours(-3))))
                .expiration(Date.from(expirationDate.toInstant(ZoneOffset.ofHours(-3))))
                .signWith(keys)
                .compact();

        return new JwtToken(accessToken, expiration);
    }

    public boolean isValidToken(String token) {
        try {
            var keys = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Jwts.parser().verifyWith(keys).build().parseSignedClaims(token);
            return true;
        } catch (UnsupportedJwtException e) {
            return false;
        }
    }

    public UUID getUserId(String token) throws UnsupportedJwtException {
        byte[] decode = Decoders.BASE64.decode(secret);
        var keys = Keys.hmacShaKeyFor(decode);
        String userId = Jwts.parser()
                .verifyWith(keys)
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();

        return UUID.fromString(userId);
    }

    /*Gera chaves para testes*/
    public static void main(String[] args) {
        MacAlgorithm alg = Jwts.SIG.HS512; //or HS384 or HS256
        System.out.println(Encoders.BASE64.encode(alg.key().build().getEncoded()));
    }
}
