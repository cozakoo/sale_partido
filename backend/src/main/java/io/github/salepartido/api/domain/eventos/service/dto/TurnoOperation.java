package io.github.salepartido.api.domain.eventos.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TurnoOperation(
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        UUID canchaUuid
) {}
