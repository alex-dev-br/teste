package br.com.fiap.restaurantusersapi.application.service.validator;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.persistence.UserPersistence;
import br.com.fiap.restaurantusersapi.application.service.validator.rule.Rule;
import br.com.fiap.restaurantusersapi.application.service.validator.rule.UserEmailUniquenessRule;
import br.com.fiap.restaurantusersapi.application.service.validator.rule.UserLoginUniquenessRule;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;
import java.util.Objects;

@Named
public record UpdateUserValidator(List<Rule<User>> rules) implements Validator<User> {

    public UpdateUserValidator {
        Objects.requireNonNull(rules);
    }

    @Inject
    public UpdateUserValidator(UserPersistence userPersistence) {
        this(List.of(new UserEmailUniquenessRule(userPersistence), new UserLoginUniquenessRule(userPersistence)));
    }

    @Override
    public ValidationResult validate(User user) {
        return rules.stream()
                .map(rule -> rule.execute(user))
                .reduce(ValidationResult::mergeErrors)
                .orElseGet(ValidationResult::new);
    }
}
