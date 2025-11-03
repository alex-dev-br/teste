package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.bootstrap;

import br.com.fiap.restaurantusersapi.application.ports.inbound.BootstrapAdminUseCase;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(BootstrapAdminProperties.class)
@Configuration
public class BootstrapAdminConfig {

    @Bean
    public ApplicationRunner bootstrapAdminRunner(
            BootstrapAdminProperties props,
            BootstrapAdminUseCase useCase
    ) {
        return args -> {
            if (!props.enabled()) return;

            // (opcional) validação rápida para evitar campos vazios em produção
            if (props.name() == null || props.name().isBlank()
                    || props.email() == null || props.email().isBlank()
                    || props.login() == null || props.login().isBlank()
                    || props.password() == null || props.password().isBlank()) {
                throw new IllegalStateException("Parâmetros de bootstrap do admin estão incompletos.");
            }

            useCase.execute(new BootstrapAdminUseCase.Command(
                    props.name(),
                    props.email(),
                    props.login(),
                    props.password(),
                    props.forceChangeOnFirstLogin()
            ));
        };
    }
}
