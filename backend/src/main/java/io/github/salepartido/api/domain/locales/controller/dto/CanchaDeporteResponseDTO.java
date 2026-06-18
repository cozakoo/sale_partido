package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;

public record CanchaDeporteResponseDTO(
    UUID deporteUuid,
    String deporteNombre,
    List<NivelHabilidadDTO> niveles
) {}
