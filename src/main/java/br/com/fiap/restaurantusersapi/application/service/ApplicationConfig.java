package br.com.fiap.restaurantusersapi.application.service;

import br.com.fiap.restaurantusersapi.application.ports.inbound.BootstrapAdminUseCase;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.CountAdminsPort;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.SaveUserWithPolicyPort;
import br.com.fiap.restaurantusersapi.application.ports.outbound.security.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    BootstrapAdminUseCase bootstrapAdminUseCase(CountAdminsPort countAdmins,
                                                SaveUserWithPolicyPort saveUserWithPolicy,
                                                PasswordEncoder passwordEncoder) {
        return new BootstrapAdminService(countAdmins, saveUserWithPolicy, passwordEncoder);
    }
}
