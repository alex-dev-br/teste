package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.admin")
public record BootstrapAdminProperties(
        boolean enabled,
        String name,
        String email,
        String login,
        String password,
        boolean forceChangeOnFirstLogin
) {}
