package br.com.fiap.restaurantusersapi.service.config;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.service.validator.CreateUserValidatorOld;
import br.com.fiap.restaurantusersapi.service.validator.ValidatorOld;
import br.com.fiap.restaurantusersapi.service.validator.rule.UserEmailUniquenessRule;
import br.com.fiap.restaurantusersapi.service.validator.rule.UserLoginUniquenessRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserBeanConfig {

    @Bean("userCreatorValidatorOld")
    public ValidatorOld<UserEntity> createUserCreatorValidatorOld(UserRepositoryJPA userRepositoryJPA) {
        return new CreateUserValidatorOld(List.of(
                new UserEmailUniquenessRule(userRepositoryJPA),
                new UserLoginUniquenessRule(userRepositoryJPA)
        ));
    }

    @Bean("userCreatorValidator")
    public br.com.fiap.restaurantusersapi.application.service.validator.Validator<User> createUserCreatorValidator(UserPersistence userPersistence) {
        return new br.com.fiap.restaurantusersapi.application.service.validator.CreateUserValidator(List.of(
                new br.com.fiap.restaurantusersapi.application.service.validator.rule.UserEmailUniquenessRule(userPersistence),
                new br.com.fiap.restaurantusersapi.application.service.validator.rule.UserLoginUniquenessRule(userPersistence)
        ));
    }
}
