package br.com.fiap.restaurantusersapi.application.service.validator.rule;

import br.com.fiap.restaurantusersapi.application.domain.user.User;
import br.com.fiap.restaurantusersapi.application.ports.outbound.repository.UserRepository;
import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;


public class UserEmailUniquenessRule implements Rule<User> {

    private final UserRepository userRepository;

    public UserEmailUniquenessRule(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ValidationResult execute(User user) {
        var optionalResult = userRepository.findByEmail(user.email().address());
        if (optionalResult.isPresent()) {
            User userFound = optionalResult.get();
            if (!userFound.uuid().equals(user.uuid())) {
                return new ValidationResult("E-mail já está sendo utilizado");
            }
        }
        return ValidationResult.SUCCESS;
    }
}
