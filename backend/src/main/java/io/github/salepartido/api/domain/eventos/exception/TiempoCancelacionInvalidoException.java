package io.github.salepartido.api.domain.eventos.exception;

public class TiempoCancelacionInvalidoException extends RuntimeException {

    public TiempoCancelacionInvalidoException() {
        super("El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas.");
    }

}
