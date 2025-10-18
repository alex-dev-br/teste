package br.com.fiap.restaurantusersapi.service.validator.rule;

import br.com.fiap.restaurantusersapi.domain.User;
import br.com.fiap.restaurantusersapi.domain.UserRepository;
import br.com.fiap.restaurantusersapi.service.validator.ValidationResult;

public class UserEmailUniquenessRule implements Rule<User> {

    private final UserRepository userRepository;

    public UserEmailUniquenessRule(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ValidationResult execute(User user) {
        var optionalResult = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (optionalResult.isPresent()) {
            User userFound = optionalResult.get();
            if (!userFound.getId().equals(user.getId())) {
                return new ValidationResult("E-mail já está sendo utilizado");
            }
        }
        return ValidationResult.SUCCESS;
    }
}
