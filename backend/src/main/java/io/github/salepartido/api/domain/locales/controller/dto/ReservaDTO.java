package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reserva asociada a un turno ocupado")
public record ReservaDTO(
    @Schema(description = "UUID de la reserva")
    UUID uuid,

    @Schema(description = "Nombre del organizador", example = "Martín")
    String nombreOrganizador,

    @Schema(description = "Deporte", example = "Fútbol")
    String deporte,

    @Schema(description = "Capacidad", example = "10")
    Integer capacidad,

    @Schema(description = "Cantidad de participantes confirmados", example = "10")
    Integer cantidadParticipantesConfirmados,

    @Schema(description = "Estado del evento", example = "CONFIRMADO")
    String estadoEvento
) {}
