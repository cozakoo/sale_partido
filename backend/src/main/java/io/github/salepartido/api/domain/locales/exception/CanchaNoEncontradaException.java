package io.github.salepartido.api.domain.locales.exception;

import java.util.UUID;

public class CanchaNoEncontradaException extends RuntimeException {
    public CanchaNoEncontradaException(UUID uuid) {
        super("Cancha con UUID " + uuid + " no encontrada");
    }
}
