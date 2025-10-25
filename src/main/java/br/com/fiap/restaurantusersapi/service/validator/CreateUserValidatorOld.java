package br.com.fiap.restaurantusersapi.service.validator;

import br.com.fiap.restaurantusersapi.infrastructure.adapters.outbound.persistence.entity.UserEntity;
import br.com.fiap.restaurantusersapi.service.validator.rule.Rule;

import java.util.List;
import java.util.Objects;

public record CreateUserValidatorOld(List<Rule<UserEntity>> rules) implements ValidatorOld<UserEntity> {

    public CreateUserValidatorOld {
        Objects.requireNonNull(rules);
    }

    @Override
    public ValidationResultOld validate(UserEntity user) {
        return rules.stream()
                .map(rule -> rule.execute(user))
                .reduce(ValidationResultOld::mergeErrors)
                .orElseGet(ValidationResultOld::new);
    }
}
