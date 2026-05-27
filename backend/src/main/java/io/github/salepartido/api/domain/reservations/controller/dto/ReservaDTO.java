package io.github.salepartido.api.domain.reservations.controller.dto;

import java.util.UUID;

public record ReservaDTO(
    UUID uuid,
    String nombreOrganizador,
    String deporte,
    Integer cantidadParticipantesConfirmados,
    String estadoEvento
) {}
