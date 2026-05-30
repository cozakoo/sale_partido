package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record SaveCanchasConfiguracionesHorariosRequest(
    @NotEmpty(message = "La lista de canchas a configurar no puede estar vacía")
    @Valid
    List<CanchaConfiguracionDTO> canchas
) {}