package br.com.fiap.restaurantusersapi.application.service.validator;

import java.util.ArrayList;
import java.util.List;

public record ValidationResult(List<String> errors) {

    public static final ValidationResult SUCCESS = new ValidationResult(List.of());

    public ValidationResult() {
        this(new ArrayList<>());
    }

    public ValidationResult(String error) {
        this();
        addError(error);
    }

    public void addError(String message) {
        this.errors.add(message);
    }

    public ValidationResult mergeErrors(ValidationResult validationResult) {
        List<String> allErrors = new ArrayList<>(this.errors);
        allErrors.addAll(validationResult.errors);
        return new ValidationResult(allErrors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public boolean isInvalid() {
        return !isValid();
    }

    @Override
    public List<String> errors() {
        return List.copyOf(errors);
    }
}