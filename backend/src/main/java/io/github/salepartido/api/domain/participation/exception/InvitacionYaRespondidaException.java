package io.github.salepartido.api.domain.participation.exception;

public class InvitacionYaRespondidaException extends RuntimeException {

    public InvitacionYaRespondidaException() {
        super("La invitación ya fue procesada anteriormente.");
    }

}
