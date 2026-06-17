package io.github.salepartido.api.domain.locales.exception;

public class CanchaNoPerteneceAlLocalException extends RuntimeException {
    public CanchaNoPerteneceAlLocalException() {
        super("La cancha no pertenece al local especificado");
    }
}
