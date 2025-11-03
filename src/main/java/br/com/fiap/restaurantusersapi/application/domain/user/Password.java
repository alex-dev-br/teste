package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Password(String value, boolean encoded) {

    public static final String NULL_OR_EMPTY_MESSAGE = "Password cannot be null or empty";
    public static final String PASSWORD_ERROR_MESSAGE =
            "Password must be at least 8 characters long, contain at least one uppercase letter, " +
                    "one lowercase letter, one number, and one special character (@$!%*?&#).";

    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$");

    public Password {
        if (value == null || value.isBlank()) {
            throw new DomainException(NULL_OR_EMPTY_MESSAGE);
        }
        // Só valida força quando a senha NÃO está codificada (hash)
        if (!encoded && !STRONG_PASSWORD_PATTERN.matcher(value).matches()) {
            throw new DomainException(PASSWORD_ERROR_MESSAGE);
        }
    }

    // Fábricas expressivas
    public static Password raw(String value)    { return new Password(value, false); }
    public static Password hashed(String value) { return new Password(value, true); }

    // Atalho legível
    public boolean isHashed() { return encoded; }

    // Construtor legacy (mantido p/ compatibilidade)
    public Password(String value) {
        this(value, false);
    }
}
