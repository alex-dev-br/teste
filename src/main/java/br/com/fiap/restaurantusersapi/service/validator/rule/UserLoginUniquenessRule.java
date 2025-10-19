package br.com.fiap.restaurantusersapi.service.validator.rule;

import br.com.fiap.restaurantusersapi.domain.User;
import br.com.fiap.restaurantusersapi.domain.UserRepository;
import br.com.fiap.restaurantusersapi.service.validator.ValidationResult;

public class UserLoginUniquenessRule implements Rule<User> {

    private final UserRepository userRepository;

    public UserLoginUniquenessRule(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ValidationResult execute(User user) {
        var optionalResult = userRepository.findByLoginIgnoreCase(user.getLogin());
        if (optionalResult.isPresent()) {
            User userFound = optionalResult.get();
            if (!userFound.getId().equals(user.getId())) {
                return new ValidationResult("Login já está sendo utilizado");
            }
        }
        return ValidationResult.SUCCESS;
    }
}
