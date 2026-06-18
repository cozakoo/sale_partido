package io.github.salepartido.api.domain.locales.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BuscarLocalesOperation(
    String ubicacion,
    String tipoDeporte,
    LocalDate fecha,
    LocalTime desde,
    LocalTime hasta
) {}
