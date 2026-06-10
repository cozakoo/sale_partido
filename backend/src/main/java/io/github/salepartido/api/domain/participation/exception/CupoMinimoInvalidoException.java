package io.github.salepartido.api.domain.participation.exception;

public class CupoMinimoInvalidoException extends RuntimeException {

    public CupoMinimoInvalidoException() {
        super("El cupo mínimo de jugadores debe ser mayor a cero.");
    }

}
