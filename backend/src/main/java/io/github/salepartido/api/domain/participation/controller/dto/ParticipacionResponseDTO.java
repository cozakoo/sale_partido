package io.github.salepartido.api.domain.participation.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.github.salepartido.api.domain.participation.model.Participacion;

public record ParticipacionResponseDTO(UUID uuid, String estado, Boolean esInvitacion, LocalDateTime fechaEstado) {

    public static ParticipacionResponseDTO from(Participacion p) {
        return new ParticipacionResponseDTO(
                p.getUuid(),
                p.getEstado().name(),
                p.getEsInvitacion(),
                p.getFechaEstado());
    }
}
