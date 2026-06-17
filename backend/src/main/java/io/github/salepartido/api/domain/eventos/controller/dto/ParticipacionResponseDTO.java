package io.github.salepartido.api.domain.eventos.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParticipacionResponseDTO(UUID uuid, String estado, Boolean esInvitacion, LocalDateTime fechaEstado) {
}
