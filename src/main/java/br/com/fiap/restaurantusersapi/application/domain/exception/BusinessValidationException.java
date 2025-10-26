package br.com.fiap.restaurantusersapi.application.domain.exception;

import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;

public class BusinessValidationException extends DomainException {

    private final ValidationResult validationResult;

    public BusinessValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
