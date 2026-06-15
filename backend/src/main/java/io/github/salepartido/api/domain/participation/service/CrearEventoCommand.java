package io.github.salepartido.api.domain.participation.service;

import java.time.Duration;
import java.util.UUID;

import io.github.salepartido.api.domain.participation.model.TipoEvento;

public record CrearEventoCommand(
        TurnoCommand turno,
        UUID organizadorId,
        String nombre,
        Integer cupoMinimo,
        Integer cupoMaximo,
        TipoEvento tipo,
        Duration limiteCancelacionParticipacion,
        UUID nivelRequeridoId) {
}
