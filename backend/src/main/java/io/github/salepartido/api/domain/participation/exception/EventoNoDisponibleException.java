package io.github.salepartido.api.domain.participation.exception;

public class EventoNoDisponibleException extends RuntimeException {

    public EventoNoDisponibleException() {
        super("El evento no está disponible para inscripción.");
    }

}
