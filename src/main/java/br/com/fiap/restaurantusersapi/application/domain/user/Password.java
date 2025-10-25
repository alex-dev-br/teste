package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Password(String value, boolean encoded) {

    public static final String PASSWORD_ERROR_MESSAGE = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&#).";
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$");

    public Password {
        if (value == null || (!encoded && !STRONG_PASSWORD_PATTERN.matcher(value).matches())) {
            throw new DomainException(PASSWORD_ERROR_MESSAGE);
        }
    }

    public Password(String value) {
        this(value, false);
    }
}
