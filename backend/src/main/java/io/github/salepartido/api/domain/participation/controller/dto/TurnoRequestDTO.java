package io.github.salepartido.api.domain.participation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record TurnoRequestDTO(
    @NotNull LocalDate fecha,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin,
    @NotNull UUID canchaUuid
) {}
