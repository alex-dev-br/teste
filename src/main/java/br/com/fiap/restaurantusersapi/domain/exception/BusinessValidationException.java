package br.com.fiap.restaurantusersapi.domain.exception;

import br.com.fiap.restaurantusersapi.service.validator.ValidationResult;

public class BusinessValidationException extends DomainException {

    private final ValidationResult validationResult;

    public BusinessValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
