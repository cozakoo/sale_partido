package io.github.salepartido.api.domain.eventos.exception;

public class ParticipanteYaRegistradoException extends RuntimeException {

    public ParticipanteYaRegistradoException() {
        super("El participante ya se encuentra registrado en este evento.");
    }

}
