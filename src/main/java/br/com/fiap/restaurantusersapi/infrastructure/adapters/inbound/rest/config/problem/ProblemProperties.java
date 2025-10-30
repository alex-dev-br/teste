package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.config.problem;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "app.problem")
public record ProblemProperties(URI baseUrl) {
}
// YAML usa "base-url", o binder mapeia para "baseUrl" automaticamente.
