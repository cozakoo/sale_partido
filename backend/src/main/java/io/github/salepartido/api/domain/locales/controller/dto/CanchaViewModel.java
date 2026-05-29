package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;

public record CanchaViewModel(
    UUID uuid,
    String nombre,
    String deporte,
    List<ConfiguracionHorarioDTO> configuracionesHorarios
) {}