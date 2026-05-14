package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;
import java.util.List;

public record CanchaDetail(
    UUID uuid,
    String nombre,
    List<ConfiguracionHorarioDTO> configuracionesHorarios
) {}