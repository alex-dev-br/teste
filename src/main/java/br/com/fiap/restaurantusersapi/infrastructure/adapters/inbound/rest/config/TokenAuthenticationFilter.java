package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security.TokenExtractorService;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.rest.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemDetailsAuthenticationEntryPoint.ATTR_AUTH_ERROR;
import static br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.ProblemDetailsAuthenticationEntryPoint.ERR_TOKEN_REVOKED;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractorService tokenExtractorService;
    private final TokenService tokenService;
    private final UserRepositoryJPA userRepositoryJPA;

    public TokenAuthenticationFilter(TokenExtractorService tokenExtractorService,
                                     TokenService tokenService,
                                     UserRepositoryJPA userRepositoryJPA) {
        this.tokenExtractorService = tokenExtractorService;
        this.tokenService = tokenService;
        this.userRepositoryJPA = userRepositoryJPA;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var token = tokenExtractorService.extractRequestToken(request);
        if (isValidToken(token)) {
            authenticateUser(token, request);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        return token != null &&
                !token.trim().isEmpty() &&
                tokenService.isValidToken(token);
    }

    private void authenticateUser(String token, HttpServletRequest request) {
        UUID userId = tokenService.getUserId(token);
        var optionalUser = userRepositoryJPA.findById(userId);

        optionalUser.ifPresent(user -> {

            int tokenPwdv = tokenService.getPasswordVersion(token);
            int currentPwdv = user.getPwdVersion();

            if (tokenPwdv != currentPwdv) {
                // Sinaliza "token revogado" para o AuthenticationEntryPoint (401 Problem Details)
                request.setAttribute(ATTR_AUTH_ERROR, ERR_TOKEN_REVOKED);
                return;   // não autentica; cairá no EntryPoint (401) em rotas protegidas
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        });
    }
}
