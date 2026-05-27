package io.github.salepartido.api.domain.reservations.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoDTO(
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String estado, // "LIBRE" o "OCUPADO"
    ReservaDTO reserva // null si está libre
) {}
