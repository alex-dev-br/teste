package br.com.fiap.restaurantusersapi.application.service.validator.rule;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;


public class UserEmailUniquenessRule implements Rule<User> {

    private final UserPersistence userPersistence;

    public UserEmailUniquenessRule(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public ValidationResult execute(User user) {
        var optionalResult = userPersistence.findByEmail(user.email().address());
        if (optionalResult.isPresent()) {
            User userFound = optionalResult.get();
            if (!userFound.uuid().equals(user.uuid())) {
                return new ValidationResult("E-mail já está sendo utilizado");
            }
        }
        return ValidationResult.SUCCESS;
    }
}
