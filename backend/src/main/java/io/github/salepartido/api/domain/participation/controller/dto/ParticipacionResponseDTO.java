package io.github.salepartido.api.domain.participation.controller.dto;

import java.util.UUID;

import io.github.salepartido.api.domain.participation.model.Participacion;

public record ParticipacionResponseDTO(UUID uuid, String estado, Boolean esInvitacion) {

    public static ParticipacionResponseDTO from(Participacion p) {
        return new ParticipacionResponseDTO(
                p.getUuid(),
                p.getEstado().name(),
                p.getEsInvitacion());
    }
}
