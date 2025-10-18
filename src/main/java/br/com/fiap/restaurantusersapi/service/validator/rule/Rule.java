package br.com.fiap.restaurantusersapi.service.validator.rule;

import br.com.fiap.restaurantusersapi.service.validator.ValidationResult;

public interface Rule <T> {
    ValidationResult execute(T t);
}
