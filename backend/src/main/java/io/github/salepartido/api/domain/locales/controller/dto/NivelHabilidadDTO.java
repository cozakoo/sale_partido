package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;

public record NivelHabilidadDTO(
    UUID uuid,
    String nombre,
    Integer orden,
    String descripcion
) {}
