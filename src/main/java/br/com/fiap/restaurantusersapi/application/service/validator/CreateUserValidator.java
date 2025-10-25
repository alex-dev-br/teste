package br.com.fiap.restaurantusersapi.application.service.validator;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.service.validator.rule.Rule;
import jakarta.inject.Named;

import java.util.List;
import java.util.Objects;

@Named
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
