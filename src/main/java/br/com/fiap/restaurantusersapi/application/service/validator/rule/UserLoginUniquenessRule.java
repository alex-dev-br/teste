package br.com.fiap.restaurantusersapi.application.service.validator.rule;

import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;
import br.com.fiap.restaurantusersapi.domain.User;
import br.com.fiap.restaurantusersapi.domain.UserRepository;

public class UserLoginUniquenessRule implements Rule<User> {

    private final UserRepository userRepository;

    public UserLoginUniquenessRule(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ValidationResult execute(User user) {
        var existing = userRepository.findByLoginIgnoreCase(user.getLogin());
        if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
            return new ValidationResult("Login já está sendo utilizado");
        }
        return ValidationResult.SUCCESS;
    }
}
