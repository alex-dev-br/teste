package br.com.fiap.restaurantusersapi.service.validator.rule;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.repository.UserRepositoryJPA;
import br.com.fiap.restaurantusersapi.service.validator.ValidationResultOld;

public class UserLoginUniquenessRule implements Rule<UserEntity> {

    private final UserRepositoryJPA userRepositoryJPA;

    public UserLoginUniquenessRule(UserRepositoryJPA userRepositoryJPA) {
        this.userRepositoryJPA = userRepositoryJPA;
    }

    @Override
    public ValidationResultOld execute(UserEntity user) {
        var existing = userRepositoryJPA.findByLoginIgnoreCase(user.getLogin());
        if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
            return new ValidationResultOld("Login já está sendo utilizado");
        }
        return ValidationResultOld.SUCCESS;
    }
}
