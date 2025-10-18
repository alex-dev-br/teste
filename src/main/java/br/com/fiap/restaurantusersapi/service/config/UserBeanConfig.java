package br.com.fiap.restaurantusersapi.service.config;

import br.com.fiap.restaurantusersapi.domain.User;
import br.com.fiap.restaurantusersapi.domain.UserRepository;
import br.com.fiap.restaurantusersapi.service.validator.CreateUserValidator;
import br.com.fiap.restaurantusersapi.service.validator.Validator;
import br.com.fiap.restaurantusersapi.service.validator.rule.UserEmailUniquenessRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserBeanConfig {

    @Bean("userCreatorValidator")
    public Validator<User> createUserCreatorValidator(UserRepository userRepository) {
        return new CreateUserValidator(List.of(new UserEmailUniquenessRule(userRepository)));
    }
}
