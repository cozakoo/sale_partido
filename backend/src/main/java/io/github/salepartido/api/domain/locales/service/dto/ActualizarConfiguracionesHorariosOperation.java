package io.github.salepartido.api.domain.locales.service.dto;

import java.util.List;
import java.util.UUID;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record ActualizarConfiguracionesHorariosOperation(
    List<CanchaConfiguracionOperation> canchas
) {
    public record CanchaConfiguracionOperation(
        UUID canchaUuid,
        Long duracionTurno,
        List<ConfiguracionDiaOperation> configuracionesDias
    ) {}

    public record ConfiguracionDiaOperation(
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin
    ) {}
}
