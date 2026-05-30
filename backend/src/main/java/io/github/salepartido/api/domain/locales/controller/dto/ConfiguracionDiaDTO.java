package io.github.salepartido.api.domain.locales.controller.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import io.github.salepartido.api.domain.locales.validation.ConfiguracionDiaValida;

@ConfiguracionDiaValida
public record ConfiguracionDiaDTO(
    DayOfWeek diaSemana,
    LocalTime horaInicio,
    LocalTime horaFin
) {}
