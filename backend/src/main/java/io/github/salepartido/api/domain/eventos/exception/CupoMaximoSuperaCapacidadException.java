package io.github.salepartido.api.domain.eventos.exception;

public class CupoMaximoSuperaCapacidadException extends RuntimeException {

    public CupoMaximoSuperaCapacidadException() {
        super("El cupo máximo no puede superar la capacidad máxima de la cancha.");
    }

}
