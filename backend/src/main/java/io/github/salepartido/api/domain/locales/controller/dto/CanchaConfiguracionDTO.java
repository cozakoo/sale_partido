package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CanchaConfiguracionDTO(
    @NotNull(message = "El UUID de la cancha es obligatorio")
    UUID canchaUuid,
    
    @NotNull(message = "La duración del turno es obligatoria")
    @Min(value = 15, message = "La duración del turno debe ser de al menos 15 minutos")
    Long duracionTurno,
    
    @NotNull(message = "Las configuraciones de los días son obligatorias")
    @Valid List<ConfiguracionDiaDTO> configuracionesDias
) {}