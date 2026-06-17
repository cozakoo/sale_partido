package io.github.salepartido.api.domain.eventos.service.dto;

import java.time.Duration;
import java.util.UUID;
import io.github.salepartido.api.domain.eventos.model.TipoEvento;

public record CrearEventoOperation(
        TurnoOperation turno,
        UUID organizadorId,
        String nombre,
        Integer cupoMinimo,
        Integer cupoMaximo,
        TipoEvento tipo,
        Duration limiteCancelacionParticipacion,
        UUID nivelRequeridoId) {
}
