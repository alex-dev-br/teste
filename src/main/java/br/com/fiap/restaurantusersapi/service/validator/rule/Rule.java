package br.com.fiap.restaurantusersapi.service.validator.rule;

import br.com.fiap.restaurantusersapi.service.validator.ValidationResultOld;

public interface Rule <T> {
    ValidationResultOld execute(T t);
}
