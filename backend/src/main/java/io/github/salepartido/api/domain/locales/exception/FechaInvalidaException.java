package io.github.salepartido.api.domain.locales.exception;

public class FechaInvalidaException extends RuntimeException {
    public FechaInvalidaException(String fecha) {
        super("Fecha inválida: " + fecha);
    }
}
