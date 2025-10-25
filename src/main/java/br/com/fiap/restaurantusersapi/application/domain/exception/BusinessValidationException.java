package br.com.fiap.restaurantusersapi.application.domain.exception;

import br.com.fiap.restaurantusersapi.service.validator.ValidationResultOld;

public class BusinessValidationException extends DomainException {

    private final ValidationResultOld validationResult;

    public BusinessValidationException(ValidationResultOld validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResultOld getValidationResult() {
        return validationResult;
    }
}
