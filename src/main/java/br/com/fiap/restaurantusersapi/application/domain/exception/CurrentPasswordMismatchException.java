package br.com.fiap.restaurantusersapi.application.domain.exception;

public class CurrentPasswordMismatchException extends DomainException {
    public CurrentPasswordMismatchException() {
        super("A senha atual fornecida está incorreta e a operação não pôde ser concluída.");
    }
}
