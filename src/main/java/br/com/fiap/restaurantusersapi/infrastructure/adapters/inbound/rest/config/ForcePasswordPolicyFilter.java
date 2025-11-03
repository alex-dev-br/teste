package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem.PasswordChangeRequiredException;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ForcePasswordPolicyFilter extends OncePerRequestFilter {

    private static final String CHANGE_PASSWORD_PATH = "/api/v1/users/change-password";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserEntity user) {
            String path = req.getRequestURI();
            boolean isChangePwd = "PUT".equalsIgnoreCase(req.getMethod()) && CHANGE_PASSWORD_PATH.equals(path);
            if (user.isPwdMustChange() && !isChangePwd) {
                // Lança exceção para o AccessDeniedHandler formatar via Problem Details
                throw new PasswordChangeRequiredException();
            }
        }
        chain.doFilter(req, res);
    }
}
