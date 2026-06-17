package io.github.salepartido.api.domain.eventos.exception;

public class NivelInsuficienteException extends RuntimeException {

    public NivelInsuficienteException() {
        super("El participante no cumple con el nivel requerido para este evento.");
    }

}
