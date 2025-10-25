package br.com.fiap.restaurantusersapi.application.domain.user;

import br.com.fiap.restaurantusersapi.application.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Email(String address) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public Email(String address) {
        if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
            throw new DomainException("Invalid email address format");
        }
        this.address = address.toLowerCase();
    }
}
