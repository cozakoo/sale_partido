package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;

import io.github.salepartido.api.domain.locales.controller.validator.DuracionTurnoValida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CanchaConfiguracionDTO(
    @NotNull(message = "El UUID de la cancha es obligatorio")
    UUID canchaUuid,
    
    @NotNull(message = "La duración del turno es obligatoria")
    @DuracionTurnoValida
    Long duracionTurno,
    
    @NotNull(message = "Las configuraciones de los días son obligatorias")
    @Valid List<ConfiguracionDiaDTO> configuracionesDias
) {}