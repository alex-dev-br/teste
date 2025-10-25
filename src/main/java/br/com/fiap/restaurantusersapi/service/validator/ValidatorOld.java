package br.com.fiap.restaurantusersapi.service.validator;

public interface ValidatorOld<T> {
    ValidationResultOld validate(T t);
}
