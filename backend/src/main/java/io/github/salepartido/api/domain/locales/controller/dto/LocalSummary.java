package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;

public record LocalSummary(
    UUID uuid,
    String nombre) {}