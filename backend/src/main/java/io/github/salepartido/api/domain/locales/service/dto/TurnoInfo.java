package io.github.salepartido.api.domain.locales.service.dto;

import java.util.UUID;

public record TurnoInfo(
    UUID uuid,
    String organizadorNombre,
    String deporte,
    Integer capacidad,
    Integer cantidadConfirmados,
    String estadoEvento
) {}
