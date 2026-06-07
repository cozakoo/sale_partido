package io.github.salepartido.api.domain.participation.exception;

public class ParticipanteYaRegistradoException extends RuntimeException {

    public ParticipanteYaRegistradoException() {
        super("El participante ya se encuentra registrado en este evento.");
    }

}
