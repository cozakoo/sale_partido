package io.github.salepartido.api.domain.locales.controller.dto;

import java.util.UUID;
import java.util.List;

public record LocalDetail(
    UUID uuid,
    String nombre,
    String ubicacion,
    List<String> deportes,
    String telefono,
    String descripcion,
    String horario,
    List<CanchaViewModel> canchas
) {}