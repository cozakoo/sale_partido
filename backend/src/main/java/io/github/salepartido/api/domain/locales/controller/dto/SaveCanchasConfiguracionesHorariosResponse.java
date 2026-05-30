package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;

public record SaveCanchasConfiguracionesHorariosResponse(
    List<CanchaConfiguracionDTO> canchas
) {}