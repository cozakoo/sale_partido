package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.List;
import java.util.UUID;

public record LocalViewModel(
    UUID uuid,
    String nombre,
    String ubicacion,
    List<String> deportesDisponibles,
    List<CanchaViewModel> canchas
) {}