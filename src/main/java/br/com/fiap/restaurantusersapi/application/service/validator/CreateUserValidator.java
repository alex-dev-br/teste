package br.com.fiap.restaurantusersapi.application.service.validator;


import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.service.validator.rule.Rule;

import java.util.List;
import java.util.Objects;

public class CreateUserValidator implements Validator<User> {

    private final List<Rule<User>> rules;

    public CreateUserValidator(List<Rule<User>> rules) {
        Objects.requireNonNull(rules);
        this.rules = rules;
    }

    @Override
    public ValidationResult validate(User user) {
        return rules.stream()
                .map(rule -> rule.execute(user))
                .reduce(ValidationResult::mergeErrors)
                .orElseGet(ValidationResult::new);
    }
}
