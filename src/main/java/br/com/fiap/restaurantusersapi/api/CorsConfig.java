package br.com.fiap.restaurantusersapi.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:}")   // lido do application-<perfil>.yml
    private String allowedOriginsProp;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //  Converte "http://front1.com,https://front2.com" em uma lista
        List<String> allowedOrigins = StringUtils.hasText(allowedOriginsProp)
                ? Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList()
                : List.of();   // se vazio => nenhum origin liberado

        registry.addMapping("/**")   // Health/Swagger também respeitam CORS ("/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
//                .allowCredentials(true)   // Se precisar enviar cookies/autorização cross-site. Domínios deverão ser listados explicitamente (sem o "*")
                .maxAge(3600);
    }
}
