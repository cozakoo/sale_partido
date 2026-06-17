package io.github.salepartido.api.domain.locales.service.dto;

import java.util.List;
import java.util.UUID;

public record CanchaDisponibilidad(
    UUID canchaUuid,
    String canchaNombre,
    Integer capacidad,
    List<TurnoSlot> slots
) {}
