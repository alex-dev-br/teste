package br.com.fiap.restaurantusersapi.config;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.service.TokenExtractorService;
import br.com.fiap.restaurantusersapi.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractorService tokenExtractorService;
    private final TokenService tokenService;
    private final UserRepositoryJPA userRepositoryJPA;

    public TokenAuthenticationFilter(TokenExtractorService tokenExtractorService, TokenService tokenService, UserRepositoryJPA userRepositoryJPA) {
        this.tokenExtractorService = tokenExtractorService;
        this.tokenService = tokenService;
        this.userRepositoryJPA = userRepositoryJPA;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = tokenExtractorService.extractRequestToken(request);
        if (isValidToken(token)) {
            autenticateUser(token);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        return token != null && !token.trim().isEmpty() &&
                !token.startsWith("Bearer ") &&
                tokenService.isValidToken(token);
    }

    private void autenticateUser(String token) {
        UUID userId = tokenService.getUserId(token);
        var optionalUser = userRepositoryJPA.findById(userId);

        optionalUser.ifPresent(user -> SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));
    }
}
