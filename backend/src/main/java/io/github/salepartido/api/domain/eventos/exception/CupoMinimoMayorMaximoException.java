package io.github.salepartido.api.domain.eventos.exception;

public class CupoMinimoMayorMaximoException extends RuntimeException {

    public CupoMinimoMayorMaximoException() {
        super("El cupo mínimo no puede ser mayor al cupo máximo.");
    }

}
