package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Disponibilidad de una cancha en un rango de fechas")
public record DisponibilidadCanchaDTO(
    @Schema(description = "UUID de la cancha")
    UUID canchaUuid,

    @Schema(description = "Nombre de la cancha", example = "Cancha 1")
    String canchaNombre,

    @Schema(description = "Capacidad de la cancha", example = "10")
    Integer capacidad,
    
    @Schema(description = "Lista de turnos generados")
    List<TurnoSlotDTO> turnos
) {}
