package br.com.fiap.restaurantusersapi.application.service.validator.rule;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;

public class UserLoginUniquenessRule implements Rule<User> {

    private final UserPersistence userPersistence;

    public UserLoginUniquenessRule(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public ValidationResult execute(User user) {
        var existing = userPersistence.findByLogin(user.login());
        if (existing.isPresent() && !existing.get().uuid().equals(user.uuid())) {
            return new ValidationResult("Login já está sendo utilizado");
        }
        return ValidationResult.SUCCESS;
    }
}
