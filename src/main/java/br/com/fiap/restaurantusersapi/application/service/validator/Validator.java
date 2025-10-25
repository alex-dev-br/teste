package br.com.fiap.restaurantusersapi.application.service.validator;

public interface Validator <T> {
    ValidationResult validate(T t);
}
