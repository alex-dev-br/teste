package br.com.fiap.restaurantusersapi.application.service.validator.rule;

import br.com.fiap.restaurantusersapi.application.service.validator.ValidationResult;

public interface Rule <T> {
    ValidationResult execute(T t);
}
