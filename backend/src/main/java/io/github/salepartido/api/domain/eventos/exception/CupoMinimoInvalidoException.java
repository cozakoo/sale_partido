package io.github.salepartido.api.domain.eventos.exception;

public class CupoMinimoInvalidoException extends RuntimeException {

    public CupoMinimoInvalidoException() {
        super("El cupo mínimo de jugadores debe ser mayor a cero.");
    }

}
