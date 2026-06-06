package io.github.salepartido.api.domain.locales.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Turno de una cancha en una fecha específica")
public record TurnoDTO(
    @Schema(description = "Fecha del turno", example = "2026-06-01")
    LocalDate fecha,

    @Schema(description = "Hora de inicio", example = "09:00")
    LocalTime horaInicio,

    @Schema(description = "Hora de fin", example = "10:00")
    LocalTime horaFin,

    @Schema(description = "Nombre de la cancha", example = "Cancha 1")
    String espacioNombre,

    @Schema(description = "Deporte (null si está libre)", example = "Fútbol")
    String deporte,

    @Schema(description = "Estado del turno", allowableValues = {"LIBRE", "OCUPADO"})
    String estado,

    @Schema(description = "Reserva asociada (null si está libre)")
    ReservaDTO reserva
) {}
