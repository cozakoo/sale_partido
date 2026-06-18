package io.github.salepartido.api.domain.eventos.exception;

public class EventoSinCuposException extends RuntimeException {

    public EventoSinCuposException() {
        super("El evento no tiene cupos disponibles.");
    }

}
