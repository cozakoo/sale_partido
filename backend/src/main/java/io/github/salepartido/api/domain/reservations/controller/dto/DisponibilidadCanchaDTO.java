package io.github.salepartido.api.domain.reservations.controller.dto;

import java.util.List;
import java.util.UUID;

public record DisponibilidadCanchaDTO(
    UUID canchaUuid,
    String canchaNombre,
    List<TurnoDTO> turnos
) {}
