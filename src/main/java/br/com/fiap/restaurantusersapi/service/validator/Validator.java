package br.com.fiap.restaurantusersapi.service.validator;

public interface Validator <T> {
    ValidationResult validate(T t);
}
