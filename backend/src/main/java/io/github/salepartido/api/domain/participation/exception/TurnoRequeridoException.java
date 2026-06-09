package io.github.salepartido.api.domain.participation.exception;

public class TurnoRequeridoException extends RuntimeException {

    public TurnoRequeridoException() {
        super("Debe seleccionar y reservar un espacio para el evento.");
    }

}
