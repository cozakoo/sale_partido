package io.github.salepartido.api.domain.locales.exception;

public class HorarioInvalidoException extends RuntimeException {
    public HorarioInvalidoException(String hora) {
        super("Horario inválido: " + hora + ". Use el formato HH:mm o HH:mm hs.");
    }
}
