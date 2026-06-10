package io.github.salepartido.api.domain.participation.exception;

public class CupoMinimoMayorMaximoException extends RuntimeException {

    public CupoMinimoMayorMaximoException() {
        super("El cupo mínimo no puede ser mayor al cupo máximo.");
    }

}
