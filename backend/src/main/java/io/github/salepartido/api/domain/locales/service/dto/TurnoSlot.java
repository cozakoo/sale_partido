package io.github.salepartido.api.domain.locales.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoSlot(
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String canchaNombre,
    String deporte,
    String estado,
    TurnoInfo turno
) {}
