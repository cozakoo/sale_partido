package io.github.salepartido.api.domain.locales.exception;

public class LocalNoEncontradoException extends RuntimeException {
    public LocalNoEncontradoException() {
        super("Local no encontrado");
    }
}
