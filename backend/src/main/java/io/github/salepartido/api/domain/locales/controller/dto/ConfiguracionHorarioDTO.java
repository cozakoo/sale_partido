package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;

public record ConfiguracionHorarioDTO(
    boolean activo,
    Long duracionTurno,
    List<ConfiguracionDiaDTO> configuracionesDias
) {}