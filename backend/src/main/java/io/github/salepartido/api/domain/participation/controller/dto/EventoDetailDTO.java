package io.github.salepartido.api.domain.participation.controller.dto;

import java.util.UUID;

import io.github.salepartido.api.domain.participation.model.Evento;

public record EventoDetailDTO(UUID uuid, String nombre, String tipo, String estado, Integer cupoMinimo, Integer cupoMaximo) {

    public static EventoDetailDTO from(Evento evento) {
        return new EventoDetailDTO(
                evento.getUuid(),
                evento.getNombre(),
                evento.getTipo() != null ? evento.getTipo().name() : null,
                evento.getEstado() != null ? evento.getEstado().name() : null,
                evento.getCupoMinimo(),
                evento.getCupoMaximo());
    }
}
