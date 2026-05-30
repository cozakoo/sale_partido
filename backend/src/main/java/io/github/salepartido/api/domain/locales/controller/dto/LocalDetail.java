package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;
import java.util.List;

public record LocalDetail(
    UUID uuid,
    String nombre,
    List<CanchaSummary> canchas
) {}