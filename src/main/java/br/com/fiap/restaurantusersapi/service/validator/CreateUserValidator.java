package br.com.fiap.restaurantusersapi.service.validator;

import br.com.fiap.restaurantusersapi.domain.User;
import br.com.fiap.restaurantusersapi.service.validator.rule.Rule;

import java.util.List;
import java.util.Objects;

public record CreateUserValidator(List<Rule<User>> rules) implements Validator<User> {

    public CreateUserValidator {
        Objects.requireNonNull(rules);
    }

    @Override
    public ValidationResult validate(User user) {
        return rules.stream()
                .map(rule -> rule.execute(user))
                .reduce(ValidationResult::mergeErrors)
                .orElseGet(ValidationResult::new);
    }
}
