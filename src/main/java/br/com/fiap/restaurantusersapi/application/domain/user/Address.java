package br.com.fiap.restaurantusersapi.application.domain.user;

public record Address(
    Long id,
    String street,
    String number,
    String complement,
    String city,
    String neighborhood,
    String state,
    String zipCode
) {
    public Address(String street, String number, String complement, String city, String neighborhood, String state, String zipCode) {
        this(null, street, number, complement, city, neighborhood, state, zipCode);
    }
}
