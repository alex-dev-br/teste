package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Restaurant Users API",
                version = "v1",
                description = "API para gestão de usuários (OWNER / CLIENT) - Tech Challenge Fase 1",
                contact = @Contact(name = "Equipe24", email = "alex_dev@outlook.com.br"),
                license = @License(name = "MIT")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
    // Apenas a anotação já registra a doc.
}
