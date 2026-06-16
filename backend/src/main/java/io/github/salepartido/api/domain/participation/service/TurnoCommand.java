package io.github.salepartido.api.domain.participation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TurnoCommand(
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        UUID canchaUuid
) {}
