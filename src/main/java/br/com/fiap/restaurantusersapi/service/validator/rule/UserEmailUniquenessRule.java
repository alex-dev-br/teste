package br.com.fiap.restaurantusersapi.service.validator.rule;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.service.validator.ValidationResultOld;

public class UserEmailUniquenessRule implements Rule<UserEntity> {

    private final UserRepositoryJPA userRepositoryJPA;

    public UserEmailUniquenessRule(UserRepositoryJPA userRepositoryJPA) {
        this.userRepositoryJPA = userRepositoryJPA;
    }

    @Override
    public ValidationResultOld execute(UserEntity user) {
        var optionalResult = userRepositoryJPA.findByEmailIgnoreCase(user.getEmail());
        if (optionalResult.isPresent()) {
            UserEntity userFound = optionalResult.get();
            if (!userFound.getId().equals(user.getId())) {
                return new ValidationResultOld("E-mail já está sendo utilizado");
            }
        }
        return ValidationResultOld.SUCCESS;
    }
}
